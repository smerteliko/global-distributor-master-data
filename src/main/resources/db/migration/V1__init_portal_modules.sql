-- V1__init_portal_modules.sql

-- Таблица ПРАВ (Что можно делать?)
CREATE TABLE permissions (
                             id VARCHAR(50) PRIMARY KEY, -- 'user:create', 'crm:lead:view'
                             description VARCHAR(255)
);

-- Таблица РОЛЕЙ (Кто есть кто?)
CREATE TABLE user_roles (
                            id VARCHAR(50) PRIMARY KEY, -- 'ROLE_ADMIN', 'ROLE_MANAGER'
                            description VARCHAR(255)
);

-- Связь: Роль -> Набор прав
CREATE TABLE role_permissions (
                                  role_id VARCHAR(50) NOT NULL REFERENCES user_roles(id),
                                  permission_id VARCHAR(50) NOT NULL REFERENCES permissions(id),
                                  PRIMARY KEY (role_id, permission_id)
);


-- ==========================================
-- 3. SEED DATA (Начальные данные)
-- ==========================================

-- 3.1 Глобальные Роли (Они пригодятся и для CRM потом)
INSERT INTO user_roles (id, description) VALUES
                                             ('ROLE_ADMIN', 'Super Administrator'),
                                             ('ROLE_MANAGER', 'General Manager'),
                                             ('ROLE_MARINE_USER', 'Marine Dept Employee'),
                                             ('ROLE_FINANCE_USER', 'Finance Dept Employee');

-- 3.2 Глобальные права (Пример на будущее)
INSERT INTO permissions (id, description) VALUES
                                              ('crm:read', 'Can read CRM data'),
                                              ('portal:access', 'Can login to portal');

INSERT INTO role_permissions (role_id, permission_id) VALUES
                                                          ('ROLE_ADMIN', 'crm:read'),
                                                          ('ROLE_ADMIN', 'portal:access');

-- ==========================================
-- 1. СОЗДАНИЕ СТРУКТУРЫ ТАБЛИЦ
-- ==========================================

-- Таблица модулей (Контейнеры)
CREATE TABLE portal_modules (
                                id VARCHAR(50) PRIMARY KEY,
                                title VARCHAR(100) NOT NULL,
                                description TEXT,
                                icon VARCHAR(50),
                                status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'MAINTENANCE', 'DISABLED'
                                sort_order INT DEFAULT 0
);

-- Таблица групп (Ссылки внутри модуля)
CREATE TABLE portal_module_groups (
                                      id VARCHAR(50) PRIMARY KEY,
                                      module_id VARCHAR(50) NOT NULL REFERENCES portal_modules(id),
                                      name VARCHAR(100) NOT NULL,
                                      url VARCHAR(255),
                                      sort_order INT DEFAULT 0
);

-- Таблица ролей (Кто имеет доступ к группе)
CREATE TABLE portal_group_required_roles (
                                             group_id VARCHAR(50) NOT NULL REFERENCES portal_module_groups(id),
                                             role_name VARCHAR(50) NOT NULL,
                                             PRIMARY KEY (group_id, role_name)
);

-- ==========================================
-- 2. НАПОЛНЕНИЕ ДАННЫМИ (SEED DATA)
-- ==========================================

-- --- MODULE: KEPLER (ACTIVE, Смешанный доступ) ---
INSERT INTO portal_modules (id, title, description, icon, status, sort_order)
VALUES ('kepler', 'Marine Traffic / Kepler', 'Vessel tracking, LEO satellite telemetry and logistics proxy.', '🚢', 'ACTIVE', 10);

-- Группа 1: Публичная (видна всем)
INSERT INTO portal_module_groups (id, module_id, name, url, sort_order)
VALUES ('marinetraffic', 'kepler', 'Marine Traffic', '/marine/docs/Marinetraffic', 1);

-- Группа 2: Приватная (только для админов)
INSERT INTO portal_module_groups (id, module_id, name, url, sort_order)
VALUES ('refineries', 'kepler', 'Refineries', '/marine/docs/Refineries', 2);

INSERT INTO portal_group_required_roles (group_id, role_name)
VALUES ('refineries', 'ROLE_ADMIN'), ('refineries', 'ROLE_MARINE_ADMIN');


-- --- MODULE: LSEG (ACTIVE, но полностью закрыт ролями) ---
INSERT INTO portal_modules (id, title, description, icon, status, sort_order)
VALUES ('lseg', 'LSEG Workspace', 'Real-time financial market data and risk analytics (Refinitiv).', '📈', 'ACTIVE', 20);

-- Группа есть, но требует спец. роль -> Модуль виден, но под замком
INSERT INTO portal_module_groups (id, module_id, name, url, sort_order)
VALUES ('lseg_main', 'lseg', 'Main API', '/lseg/docs/main', 1);

INSERT INTO portal_group_required_roles (group_id, role_name)
VALUES ('lseg_main', 'ROLE_FINANCE_USER');


-- --- MODULE: AIRBUS (MAINTENANCE, Групп нет) ---
-- Так как групп нет -> hasAccess будет false -> Замок
INSERT INTO portal_modules (id, title, description, icon, status, sort_order)
VALUES ('airbus', 'Airbus Defence & Space', 'Geospatial intelligence and OneAtlas satellite imagery ingestion.', '🌍', 'MAINTENANCE', 30);


-- --- MODULE: OPENWEATHERMAP (MAINTENANCE, Групп нет) ---
-- Так как групп нет -> hasAccess будет false -> Замок
INSERT INTO portal_modules (id, title, description, icon, status, sort_order)
VALUES ('openweathermap', 'OpenWeatherMap', 'Global weather forecasting API for logistics planning.', '🌦️', 'MAINTENANCE', 40);