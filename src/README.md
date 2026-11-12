# Процедурный модуль системы удалённой экспертной поддержки (КТ2)

## Требования
- Python 3.12+ (процедурный стиль: без классов, `@dataclass`, `Enum`, без изменяемых глобальных переменных).
- Только стандартная библиотека (`datetime`, `random`).

## Запуск
Из корня репозитория:

```bash
python src/main.py
```

## Операции (таблица 6.1 отчёта, задачи BPMN `models/bpmn.drawio`)
| № | Задача на BPMN | Функция |
|---|----------------|---------|
| 1 | Сформировать карточку инцидента | `create_incident()` |
| 2 | Приоритизировать очередь инцидентов | `prioritize_incidents_queue()` — Алгоритм 2, сортировка слиянием, O(n log n) |
| 3 | Подобрать эксперта по профилю | `assign_best_expert()` — Алгоритм 1, O(n·m·s); `create_assignment()` |
| 4 | Установить P2P WebRTC-сессию | `setup_webrtc_session()` |
| 5 | Нанести AR-метку на видеокадр | `process_ar_annotation()` |
| 6 | Оформить протокол заключения | `generate_consultation_protocol()` |

## Соответствие модели данных (`models/er_logical.drawio`)
Словари в коде повторяют сущности модели: `USER` (+`USER_SKILL`/`SKILL` → список `skills`),
`EQUIPMENT`, `PRIORITY` (`get_priority_weight`), `INCIDENT` (+`INCIDENT_SKILL` → `required_skills`),
`EXPERT_ASSIGNMENT`, `SESSION` (FK `assignment_id`), `ANNOTATION` (`x1, y1, x2, y2`), `PROTOCOL` (FK `session_id`).
