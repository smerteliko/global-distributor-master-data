-- V1__init_portal_modules.sql

-- ==========================================
-- 1. SECURITY CORE (Таблицы безопасности)
-- ==========================================

-- Таблица ПРАВ
CREATE TABLE permissions (
                             id VARCHAR(50) PRIMARY KEY,
                             description VARCHAR(255)
);

-- Таблица РОЛЕЙ
CREATE TABLE user_roles (
                            id VARCHAR(50) PRIMARY KEY,
                            description VARCHAR(255)
);

-- Связь: Роль -> Набор прав
CREATE TABLE role_permissions (
                                  role_id VARCHAR(50) NOT NULL REFERENCES user_roles(id),
                                  permission_id VARCHAR(50) NOT NULL REFERENCES permissions(id),
                                  PRIMARY KEY (role_id, permission_id)
);

-- ==========================================
-- 2. PORTAL MODULES (Таблицы модулей)
-- ==========================================

CREATE TABLE portal_modules (
                                id VARCHAR(50) PRIMARY KEY,
                                title VARCHAR(100) NOT NULL,
                                description TEXT,
                                icon VARCHAR(50),
                                status VARCHAR(20) DEFAULT 'ACTIVE',
                                sort_order INT DEFAULT 0
);

CREATE TABLE portal_module_groups (
                                      id VARCHAR(50) PRIMARY KEY,
                                      module_id VARCHAR(50) NOT NULL REFERENCES portal_modules(id),
                                      name VARCHAR(100) NOT NULL,
                                      url VARCHAR(255),
                                      sort_order INT DEFAULT 0
);

-- СВЯЗЬ: Группа портала -> Глобальная роль
CREATE TABLE portal_group_required_roles (
                                             group_id VARCHAR(50) NOT NULL REFERENCES portal_module_groups(id),
                                             role_id VARCHAR(50) NOT NULL REFERENCES user_roles(id),
                                             PRIMARY KEY (group_id, role_id)
);

-- ==========================================
-- 3. SEED DATA (Данные)
-- ==========================================

-- 3.1. Создаем РОЛИ (Важно создать их первыми!)
INSERT INTO user_roles (id, description) VALUES
                                             ('ROLE_ADMIN', 'Super Administrator'),
                                             ('ROLE_MARINE_ADMIN', 'Head of Marine Dept'),
                                             ('ROLE_MARINE_USER', 'Marine Dept Employee'),
                                             ('ROLE_FINANCE_USER', 'Finance Dept Employee');

-- 3.2. Создаем ПРАВА (Пример)
INSERT INTO permissions (id, description) VALUES ('portal:access', 'Access to portal');
INSERT INTO role_permissions (role_id, permission_id) VALUES ('ROLE_ADMIN', 'portal:access');

-- 3.3. Создаем МОДУЛИ и ГРУППЫ

-- --- KEPLER ---
INSERT INTO portal_modules (id, title, description, icon, status, sort_order)
VALUES ('kepler', 'Marine Traffic / Kepler', 'Vessel tracking, LEO satellite telemetry.', '🚢', 'ACTIVE', 10);

-- Группа Marine Traffic (Публичная, без ролей)
INSERT INTO portal_module_groups (id, module_id, name, url, sort_order)
VALUES ('marinetraffic', 'kepler', 'Marine Traffic', '/marine/docs/Marinetraffic', 1);

-- Группа Refineries (Требует ROLE_ADMIN или ROLE_MARINE_ADMIN)
INSERT INTO portal_module_groups (id, module_id, name, url, sort_order)
VALUES ('refineries', 'kepler', 'Refineries', '/marine/docs/Refineries', 2);

-- Привязка ролей к Refineries
INSERT INTO portal_group_required_roles (group_id, role_id) VALUES
                                                                ('refineries', 'ROLE_ADMIN'),
                                                                ('refineries', 'ROLE_MARINE_ADMIN');


-- --- LSEG ---
INSERT INTO portal_modules (id, title, description, icon, status, sort_order)
VALUES ('lseg', 'LSEG Workspace', 'Real-time financial market data.', '📈', 'ACTIVE', 20);

INSERT INTO portal_module_groups (id, module_id, name, url, sort_order)
VALUES ('lseg_main', 'lseg', 'Main API', '/lseg/docs/main', 1);

-- Привязка роли к LSEG (Нужен ROLE_FINANCE_USER)
INSERT INTO portal_group_required_roles (group_id, role_id) VALUES
    ('lseg_main', 'ROLE_FINANCE_USER');


-- --- AIRBUS (Maintenance) ---
INSERT INTO portal_modules (id, title, description, icon, status, sort_order)
VALUES ('airbus', 'Airbus Defence & Space', 'Geospatial intelligence.', '🌍', 'MAINTENANCE', 30);


-- --- OPENWEATHERMAP (Maintenance) ---
INSERT INTO portal_modules (id, title, description, icon, status, sort_order)
VALUES ('openweathermap', 'OpenWeatherMap', 'Global weather forecasting.', '🌦️', 'MAINTENANCE', 40);