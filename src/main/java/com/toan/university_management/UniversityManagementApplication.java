package com.toan.university_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class UniversityManagementApplication {

	// All tables managed by JPA entities
	private static final Set<String> KNOWN_TABLES = Set.of(
			"building", "floor", "room",
			"department", "major", "subject",
			"teacher", "student",
			"class_group", "subject_class", "class_schedule",
			"exam_schedule", "enrollment",
			"user", "role", "permission", "user_role", "role_permission",
			"invalidated_token"
	);

	public static void main(String[] args) {
		cleanDatabase();
		SpringApplication.run(UniversityManagementApplication.class, args);
	}

	private static void cleanDatabase() {
		String url = "jdbc:mysql://localhost:3306/university?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
		String user = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "root";
		String pass = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "123456";

		try (Connection conn = DriverManager.getConnection(url, user, pass);
			 Statement stmt = conn.createStatement()) {
			stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

			// 1. Check for any legacy VARCHAR/CHAR columns in numeric FK / PK fields
			boolean needRecreate = false;
			String checkQuery = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS " +
					"WHERE TABLE_SCHEMA = 'university' AND (" +
					"COLUMN_NAME IN ('class_group_id', 'department_id', 'major_id', 'building_id', 'homeroom_teacher_id', 'teacher_id', 'subject_class_id', 'subject_id') " +
					"OR (TABLE_NAME IN ('role', 'permission') AND COLUMN_NAME = 'id' AND DATA_TYPE IN ('varchar', 'char')) " +
					"OR (TABLE_NAME = 'user_role' AND COLUMN_NAME = 'role_code') " +
					"OR (TABLE_NAME = 'role_permission' AND COLUMN_NAME IN ('role_code', 'permission_code')))";
			try (ResultSet rs = stmt.executeQuery(checkQuery)) {
				while (rs.next()) {
					String tableName = rs.getString("TABLE_NAME");
					String colName = rs.getString("COLUMN_NAME");
					String type = rs.getString("DATA_TYPE");
					if ("varchar".equalsIgnoreCase(type) || "char".equalsIgnoreCase(type) || "role_code".equalsIgnoreCase(colName) || "permission_code".equalsIgnoreCase(colName)) {
						needRecreate = true;
						break;
					}
				}
			}

			if (needRecreate) {
				System.out.println(">>> Incompatible DB schema detected (VARCHAR FK/PKs or legacy identity tables). Dropping legacy tables for BIGINT migration...");
				String[] legacyTables = {
					"user_role", "role_permission", "role", "permission",
					"enrollment", "class_schedule", "course_class", "subject_class", "course", "class_group",
					"exam_schedule", "student", "teacher", "subject", "major", "department",
					"room", "floor", "building"
				};
				for (String t : legacyTables) {
					stmt.execute("DROP TABLE IF EXISTS `" + t + "`");
				}
			}

			// 2. Drop orphan tables that have no corresponding JPA entity
			List<String> orphanTables = new ArrayList<>();
			ResultSet allTables = stmt.executeQuery(
					"SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
					"WHERE TABLE_SCHEMA = 'university' AND TABLE_TYPE = 'BASE TABLE'");
			while (allTables.next()) {
				String tableName = allTables.getString("TABLE_NAME").toLowerCase();
				if (!KNOWN_TABLES.contains(tableName)) {
					orphanTables.add(tableName);
				}
			}
			allTables.close();

			if (!orphanTables.isEmpty()) {
				System.out.println(">>> Dropping orphan tables (no JPA entity): " + orphanTables);
				for (String t : orphanTables) {
					stmt.execute("DROP TABLE IF EXISTS `" + t + "`");
				}
			}

			stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
		} catch (Exception e) {
			// Database may not exist yet — Spring Boot JPA will create it
		}
	}
}

