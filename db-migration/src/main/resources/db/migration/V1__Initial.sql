CREATE TABLE intraportal.users
(
    id                  int generated always as identity PRIMARY KEY,
    username            character varying(255) UNIQUE   NOT NULL,
    password            character varying(255)          NOT NULL,
    enabled             boolean                         NOT NULL,
    first_name          character varying(255),
    last_name           character varying(255),
    email               character varying(255)          NOT NULL,
    created             timestamp                       NOT NULL DEFAULT NOW(),
    last_login          timestamp,
    created_date        timestamp                       NOT NULL DEFAULT NOW(),
    created_by          varchar(255),
    modified_date       timestamp,
    modified_by         varchar(255)
);

CREATE TABLE intraportal.roles
(
    id          int generated always as identity PRIMARY KEY,
    name        character varying(255) UNIQUE NOT NULL,
    description character varying(255)
);

CREATE TABLE intraportal.permissions
(
    id          int generated always as identity PRIMARY KEY,
    name        character varying(255) UNIQUE NOT NULL,
    description character varying(255)
);

CREATE TABLE intraportal.user_roles
(
    id      int generated always as identity PRIMARY KEY,
    user_id integer REFERENCES Users,
    role_id integer REFERENCES Roles,
    UNIQUE(user_id, role_id)
);

CREATE TABLE intraportal.roles_permissions
(
    id            int generated always as identity PRIMARY KEY,
    role_id       integer REFERENCES Roles,
    permission_id integer REFERENCES Permissions,
    UNIQUE(role_id, permission_id)
);

INSERT INTO intraportal.roles ("name", description)
VALUES ('ADMIN', 'Administrative role')
    ON CONFLICT DO NOTHING;

INSERT INTO intraportal.roles ("name", description)
VALUES ('VIEWER', 'Viewer role')
    ON CONFLICT DO NOTHING;

WITH admin_user AS (
INSERT INTO intraportal.users (username, "password", enabled, first_name, last_name, email)
values ('admin',
    '$argon2id$v=19$m=64,t=2,p=4$R3RGUGtWZThGYllNbzZLUQ$m7A5xN+LxzsyfWVkDD28by1Jzb5hpNXq15yCWV4xiYtPUS3oCgZm3XY5GEVKfw/1lI43x5dUIAw5Q9SFqX3dow',
    true,
    'Admin',
    'Admin',
    'admin@admin.com')
ON CONFLICT DO NOTHING
    RETURNING id
    ),
    admin_role AS (
SELECT id AS admin_id
FROM intraportal.roles AS r
where r.name = 'ADMIN'
    )
INSERT
INTO intraportal.user_roles (user_id, role_id)
VALUES ((select id from admin_user), (select admin_id from admin_role))
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS intraportal.action_log
(
    id              int generated always as identity PRIMARY KEY,
    actor           character varying(255) NOT NULL,
    domain          character varying(32) NOT NULL,
    action          character varying(32) NOT NULL,
    session_id      varchar(255),
    created_date    timestamp NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS intraportal.session_log
(
    id              int generated always as identity PRIMARY KEY,
    username        character varying(255) NOT NULL,
    action          character varying(32) NOT NULL,
    session_id      varchar(255),
    created_date    timestamp NOT NULL DEFAULT NOW()
);

CREATE TABLE intraportal.server_parameters
(
    id                  int generated always as identity PRIMARY KEY,
    param_name          character varying(255) UNIQUE   NOT NULL,
    param_value         character varying(255)          NOT NULL,
    param_type          character varying(255)          NOT NULL,
    param_group         character varying(255)          NOT NULL,
    created_date        timestamp                       NOT NULL DEFAULT NOW(),
    created_by          varchar(255),
    modified_date       timestamp,
    modified_by         varchar(255)
);