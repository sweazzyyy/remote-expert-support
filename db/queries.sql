-- =====================================================================
-- Проверочные запросы (КТ4): состояние БД и результаты операций.
--   psql -U res_app -d remote_expert -f db/queries.sql
-- Номера запросов используются в протоколе tests/test_cases.md.
-- =====================================================================

-- Q1. Очередь инцидентов, ожидающих эксперта, по убыванию веса приоритета
--     (эталон для Алгоритма 2; при равном весе — порядок поступления).
SELECT i.incident_id, i.priority_level, p.weight, i.title, i.created_at
FROM incident i
JOIN priority p ON p.priority_level = i.priority_level
WHERE i.status = 'created'
ORDER BY p.weight DESC, i.created_at, i.incident_id;

-- Q2. Оценка каждого активного эксперта для каждого ожидающего инцидента:
--     score = совпавшие навыки · 100 + рейтинг (эталон для Алгоритма 1).
SELECT i.incident_id, u.user_id AS expert_id, u.full_name,
       count(us.skill_id)                 AS matched_skills,
       count(us.skill_id) * 100 + u.rating AS score
FROM incident i
CROSS JOIN app_user u
LEFT JOIN incident_skill isk ON isk.incident_id = i.incident_id
LEFT JOIN user_skill us      ON us.user_id = u.user_id AND us.skill_id = isk.skill_id
WHERE i.status = 'created' AND u.role = 'expert' AND u.is_active
GROUP BY i.incident_id, u.user_id, u.full_name, u.rating
ORDER BY i.incident_id, score DESC;

-- Q3. Статусы всех инцидентов
SELECT incident_id, priority_level, status, title FROM incident ORDER BY incident_id;

-- Q4. Полная цепочка по инциденту: назначение → сеанс → метки → протокол
SELECT i.incident_id, i.status,
       a.assignment_id, a.expert_id, a.score,
       s.session_id, s.room_id,
       (SELECT count(*) FROM annotation an WHERE an.session_id = s.session_id) AS annotations,
       pr.protocol_id, pr.closed_at
FROM incident i
LEFT JOIN expert_assignment a ON a.incident_id = i.incident_id
LEFT JOIN webrtc_session s    ON s.assignment_id = a.assignment_id
LEFT JOIN protocol pr         ON pr.session_id = s.session_id
ORDER BY i.incident_id;

-- Q5. AR-метки сеансов
SELECT annotation_id, session_id, tool_type, x1, y1, x2, y2, color
FROM annotation ORDER BY annotation_id;

-- Q6. Количество строк в таблицах (проверка, что неуспешная операция ничего не записала)
SELECT 'incident' AS tbl, count(*) FROM incident
UNION ALL SELECT 'expert_assignment', count(*) FROM expert_assignment
UNION ALL SELECT 'webrtc_session',    count(*) FROM webrtc_session
UNION ALL SELECT 'annotation',        count(*) FROM annotation
UNION ALL SELECT 'protocol',          count(*) FROM protocol;

-- Q7. Согласованность статусов: resolved ⇔ есть протокол, assigned ⇔ есть назначение без протокола.
--     Пустой результат — данные согласованы.
SELECT i.incident_id, i.status, a.assignment_id, pr.protocol_id
FROM incident i
LEFT JOIN expert_assignment a ON a.incident_id = i.incident_id
LEFT JOIN webrtc_session s    ON s.assignment_id = a.assignment_id
LEFT JOIN protocol pr         ON pr.session_id = s.session_id
WHERE (i.status = 'created'  AND a.assignment_id IS NOT NULL)
   OR (i.status = 'assigned' AND (a.assignment_id IS NULL OR pr.protocol_id IS NOT NULL))
   OR (i.status = 'resolved' AND pr.protocol_id IS NULL);

-- Q8. Роли: назначенный эксперт — эксперт, автор инцидента — инженер.
--     Пустой результат — правило соблюдено.
SELECT 'assignment' AS what, a.assignment_id AS id, u.role
FROM expert_assignment a JOIN app_user u ON u.user_id = a.expert_id
WHERE u.role <> 'expert'
UNION ALL
SELECT 'incident', i.incident_id, u.role
FROM incident i JOIN app_user u ON u.user_id = i.engineer_id
WHERE u.role <> 'engineer';
