package org.intraportal.dbmigration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.system.SystemProperties;

import javax.sql.DataSource;

import static org.springframework.boot.WebApplicationType.NONE;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class DbMigrationApplication {

	private final static String HELP = "Application usage: java  -Ddb.url=server_address -Ddb.username=username -Ddb.password=password -jar \"db-migration.jar\"";

	public static void main(String[] args) {
		SpringApplication application =
				new SpringApplicationBuilder(DbMigrationApplication.class)
						.web(NONE).build();

		application.setBannerMode(Banner.Mode.OFF);
		application.run(args);

		Flyway flyway = Flyway.configure()
				.schemas("intraportal")
				.createSchemas(Boolean.TRUE)
				.dataSource(getDataSource())
				.load();
		flyway.migrate();
	}

	public static DataSource getDataSource() {
		String username = SystemProperties.get("db.username");
		if(username == null || username.equals("")) {
			System.err.println("Missing username parameter");
			System.out.println(HELP);
			System.exit(0);
		}

		String password = SystemProperties.get("db.password");
		if(password == null || password.equals("")) {
			System.err.println("Missing password parameter");
			System.out.println(HELP);
			System.exit(0);
		}

		String url = SystemProperties.get("db.url");
		if(url == null || url.equals("")) {
			System.err.println("Missing url parameter");
			System.out.println(HELP);
			System.exit(0);
		}

		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName("org.postgresql.Driver");
		dataSourceBuilder.url(url);
		dataSourceBuilder.username(username);
		dataSourceBuilder.password(password);
		return dataSourceBuilder.build();
	}

}
