-- =====================================================================
-- Демонстрационные данные (КТ4). Совпадают с входными данными КТ2/КТ3
-- (src/main.py, Main.loadData), поэтому результаты обоих алгоритмов
-- можно сверить с процедурной и ООП-версиями.
-- Загружать после schema.sql на пустую схему:
--   psql -U res_app -d remote_expert -f db/seed.sql
-- Исходное состояние для тестов: все инциденты в статусе created,
-- назначений, сеансов, меток и протоколов нет.
-- =====================================================================

BEGIN;

INSERT INTO priority (priority_level, weight) VALUES
    ('critical', 100),
    ('high',      75),
    ('medium',    50),
    ('low',       25);

INSERT INTO skill (skill_id, name) VALUES
    (1, 'компрессоры'),
    (2, 'высокое_давление'),
    (3, 'электроника'),
    (4, 'автоматика'),
    (5, 'датчики'),
    (6, 'механика'),
    (7, 'электродвигатели');

INSERT INTO equipment (equipment_id, serial_num, model, location) VALUES
    (501, 'VSH-2.3-0417', 'ВШ-2,3/400',             'Цех 3'),
    (502, 'DD-110-2291',  'Датчик давления ДД-110', 'Цех 1'),
    (503, 'AD-75-0042',   'Электродвигатель АД-75', 'Насосная');

INSERT INTO app_user (user_id, full_name, email, role, rating, is_active) VALUES
    (101, 'Петров П.П.',      'petrov@example.org',  'expert',   5.00, true),
    (102, 'Сидоров С.С.',     'sidorov@example.org', 'expert',   4.80, true),
    -- неактивный эксперт: единственный, кто знает «механику» и «электродвигатели»
    (103, 'Иванов И.И.',      'ivanov@example.org',  'expert',   4.50, false),
    (201, 'Инженер цеха 3',   'eng201@example.org',  'engineer', NULL, true),
    (202, 'Инженер цеха 1',   'eng202@example.org',  'engineer', NULL, true),
    (203, 'Инженер насосной', 'eng203@example.org',  'engineer', NULL, true);

INSERT INTO user_skill (user_id, skill_id) VALUES
    (101, 1), (101, 2),
    (102, 3), (102, 4), (102, 5),
    (103, 6), (103, 7);

INSERT INTO incident (incident_id, equipment_id, engineer_id, priority_level,
                      title, description, category, status, created_at) VALUES
    (1001, 501, 201, 'high',     'Утечка масла и вибрация компрессора',
           'Подтекание масла из-под крышки, вибрация выше нормы', 'компрессоры', 'created', '2026-11-27 09:00:00'),
    (1002, 502, 202, 'medium',   'Сбой датчика давления',
           'Показания датчика скачут, ошибка на панели', 'электроника', 'created', '2026-11-27 09:05:00'),
    -- случай «эксперт не найден»: нужные навыки только у неактивного эксперта
    (1003, 503, 203, 'critical', 'Критический перегрев двигателя',
           'Температура корпуса выше 110 °C, срабатывает защита', 'электродвигатели', 'created', '2026-11-27 09:10:00'),
    (1004, 501, 201, 'low',      'Плановая проверка клапана',
           'Требуется консультация по регулировке клапана', 'компрессоры', 'created', '2026-11-27 09:15:00');

-- порядок навыков внутри инцидента задаётся порядком вставки (skill_id)
INSERT INTO incident_skill (incident_id, skill_id) VALUES
    (1001, 1), (1001, 2),
    (1002, 3), (1002, 5),
    (1003, 6), (1003, 7),
    (1004, 1);

-- явные идентификаторы выше не сдвигают IDENTITY — продвигаем вручную
SELECT setval(pg_get_serial_sequence('app_user', 'user_id'),          (SELECT max(user_id) FROM app_user));
SELECT setval(pg_get_serial_sequence('skill', 'skill_id'),            (SELECT max(skill_id) FROM skill));
SELECT setval(pg_get_serial_sequence('equipment', 'equipment_id'),    (SELECT max(equipment_id) FROM equipment));
SELECT setval(pg_get_serial_sequence('incident', 'incident_id'),      (SELECT max(incident_id) FROM incident));
SELECT setval(pg_get_serial_sequence('expert_assignment', 'assignment_id'), 4000, true);
SELECT setval(pg_get_serial_sequence('webrtc_session', 'session_id'),       7000, true);
SELECT setval(pg_get_serial_sequence('annotation', 'annotation_id'),        9000, true);
SELECT setval(pg_get_serial_sequence('protocol', 'protocol_id'),            3000, true);

COMMIT;
