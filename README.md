
# Intraportal - a framework for developing intranet web application.
Tech stack used:
- [Spring boot 2.7.4](https://start.spring.io/) - a rapid JAVA application development framework.
- [Thymeleaf](https://www.thymeleaf.org/) - Thymeleaf is a modern server-side Java template engine for both web and standalone environments.
- [Vue.js](https://vuejs.org/) - a progressive JavaScript framework.
- [JQuery](https://jquery.com/) - is a fast, small, and feature-rich JavaScript library.
- [Bootstrap](https://getbootstrap.com/) - powerful, extensible, and feature-packed frontend toolkit.
- [Font-awesome](https://fontawesome.com/) - is the internet's icon library and toolkit.
- [Datatables](https://datatables.net/) - is a plug-in for the jQuery Javascript library.
- [PostgreSql 15.2](https://www.postgresql.org/) - is a powerful, open source object-relational database.
- [Flyway](https://flywaydb.org/) - is a DB versioning tool.
- [Maven](https://maven.apache.org/) - is a dependency management software and build tool for JAVA applications.

## Features
- User administration panel
- User auditing log
- Server config
  - SMTP settings
  - NTP settings
  - Network settings
- User profile
- Three level roles
- System information

## Building
```
mvn clean install
```
Provide correct paths for JAVA installation and logging directory in *.sh scripts
- Edit \webtool\target\intraportal.sh file.
- Edit \db-migration\target\db-migration.sh file.
- Run \db-migration\target\db-migration.sh.
- Run \webtool\target\intraportal.sh.
