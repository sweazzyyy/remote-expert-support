"""
Процедурный модуль системы удалённой экспертной поддержки (КТ2).

Структуры данных (dict/list) повторяют логическую модель данных models/er_logical.drawio:
  USER, SKILL, USER_SKILL, EQUIPMENT, PRIORITY, INCIDENT, INCIDENT_SKILL,
  EXPERT_ASSIGNMENT, SESSION, ANNOTATION, PROTOCOL.

Автоматизируемые операции (таблица 6.1 отчёта, задачи на BPMN models/bpmn.drawio):
  1. create_incident()                 — «Сформировать карточку инцидента»
  2. prioritize_incidents_queue()      — «Приоритизировать очередь инцидентов» (Алгоритм 2)
  3. assign_best_expert() +
     create_assignment()               — «Подобрать эксперта по профилю» (Алгоритм 1)
  4. setup_webrtc_session()            — «Установить P2P WebRTC-сессию»
  5. process_ar_annotation()           — «Нанести AR-метку на видеокадр»
  6. generate_consultation_protocol()  — «Оформить протокол заключения»
"""
import datetime
import random


def get_priority_weight(priority_level):
    """
    Справочник PRIORITY (priority_level -> weight).
    Вес вынесен в отдельную сущность модели, чтобы в INCIDENT не было
    транзитивной зависимости incident_id -> priority_level -> weight (3НФ).
    """
    if priority_level == "critical":
        return 100
    if priority_level == "high":
        return 75
    if priority_level == "medium":
        return 50
    if priority_level == "low":
        return 25
    raise ValueError(f"Неизвестный уровень приоритета: {priority_level}")


def create_incident(incident_id, equipment_id, engineer_id, title, description,
                    category, required_skills, priority_level):
    """
    Операция 1: создание первичной карточки инцидента (сущность INCIDENT).
    required_skills — список навыков, в модели это связь INCIDENT_SKILL (M:N с SKILL).
    """
    incident = {
        "incident_id": incident_id,
        "equipment_id": equipment_id,          # FK -> EQUIPMENT
        "engineer_id": engineer_id,            # FK -> USER (роль engineer)
        "title": title,
        "description": description,
        "category": category,
        "required_skills": required_skills,    # INCIDENT_SKILL
        "priority_level": priority_level,      # FK -> PRIORITY
        "weight": get_priority_weight(priority_level),  # читается из PRIORITY
        "status": "created",
        "created_at": datetime.datetime.now().isoformat(),
    }
    return incident


def assign_best_expert(incident_skills, experts_list):
    """
    АЛГОРИТМ 1: подбор лучшего эксперта по совпадению навыков и рейтингу.

    Временная сложность: O(n * m * s), где
      n — число экспертов, m — число навыков инцидента,
      s — максимальное число навыков у одного эксперта
      (проверка `skill in expert["skills"]` — линейный поиск по списку, O(s)).
    Память: O(1) — только скалярные переменные.
    """
    best_expert = None
    max_score = -1

    for expert in experts_list:                     # цикл по экспертам (n)
        if not expert["is_active"]:
            continue

        matched_skills_count = 0
        for skill in incident_skills:               # цикл по навыкам инцидента (m)
            if skill in expert["skills"]:           # линейный поиск в списке (s)
                matched_skills_count += 1

        score = matched_skills_count * 100 + expert["rating"]

        if score > max_score:
            max_score = score
            best_expert = expert

    if best_expert is None or max_score < 100:
        # ни одного активного эксперта или ни одного совпавшего навыка
        return None, 0
    return best_expert, max_score


def create_assignment(assignment_id, incident, expert, score):
    """ Фиксация результата Алгоритма 1 (сущность EXPERT_ASSIGNMENT). """
    incident["status"] = "assigned"
    return {
        "assignment_id": assignment_id,
        "incident_id": incident["incident_id"],     # FK -> INCIDENT
        "expert_id": expert["user_id"],             # FK -> USER (роль expert)
        "score": score,
        "assigned_at": datetime.datetime.now().isoformat(),
    }


def merge_incidents(left, right):
    """ Слияние двух списков, отсортированных по убыванию weight. """
    merged = []
    i = 0
    j = 0
    while i < len(left) and j < len(right):
        if left[i]["weight"] >= right[j]["weight"]:
            merged.append(left[i])
            i += 1
        else:
            merged.append(right[j])
            j += 1
    while i < len(left):
        merged.append(left[i])
        i += 1
    while j < len(right):
        merged.append(right[j])
        j += 1
    return merged


def prioritize_incidents_queue(queue):
    """
    АЛГОРИТМ 2 (операция «Приоритизировать очередь инцидентов»):
    сортировка очереди по убыванию веса приоритета слиянием (Merge Sort).
    Временная сложность: O(n log n), n — число инцидентов в очереди.
    Память: O(n) — временные подсписки при слиянии (+ O(log n) стек рекурсии).
    """
    if len(queue) <= 1:                 # базовый случай рекурсии
        return queue

    mid = len(queue) // 2
    left_half = prioritize_incidents_queue(queue[:mid])
    right_half = prioritize_incidents_queue(queue[mid:])
    return merge_incidents(left_half, right_half)


def setup_webrtc_session(session_id, assignment):
    """
    Операция 4: установление WebRTC P2P-сессии (сущность SESSION).
    Сессия ссылается на назначение (FK assignment_id), через него
    однозначно определяются инцидент и эксперт.
    """
    room_id = f"room_webrtc_{assignment['incident_id']}_{random.randint(1000, 9999)}"
    return {
        "session_id": session_id,
        "assignment_id": assignment["assignment_id"],   # FK -> EXPERT_ASSIGNMENT
        "room_id": room_id,
        "quality": "HD_1080P_60FPS",
        "established_at": datetime.datetime.now().isoformat(),
    }


def process_ar_annotation(annotation_id, session_id, tool_type, x1, y1, x2, y2, color):
    """
    Операция 5: нанесение AR-метки экспертом (сущность ANNOTATION).
    Координаты хранятся отдельными скалярными атрибутами (1НФ).
    """
    return {
        "annotation_id": annotation_id,
        "session_id": session_id,       # FK -> SESSION
        "tool_type": tool_type,         # 'arrow', 'rect', 'line'
        "x1": x1,
        "y1": y1,
        "x2": x2,
        "y2": y2,
        "color": color,
        "created_at": datetime.datetime.now().isoformat(),
    }


def generate_consultation_protocol(protocol_id, session, incident, notes):
    """
    Операция 6: завершение сеанса и фиксация протокола (сущность PROTOCOL).
    Протокол ссылается на сессию (1:1); инцидент переводится в статус resolved.
    """
    incident["status"] = "resolved"
    return {
        "protocol_id": protocol_id,
        "session_id": session["session_id"],    # FK -> SESSION
        "expert_notes": notes,
        "closed_at": datetime.datetime.now().isoformat(),
    }


def find_by_id(items, key, value):
    """ Поиск записи в списке по значению ключа. """
    for item in items:
        if item[key] == value:
            return item
    return None


def main():
    print("=" * 70)
    print("  СИСТЕМА УДАЛЁННОЙ ЭКСПЕРТНОЙ ПОДДЕРЖКИ (ПРОЦЕДУРНЫЙ МОДУЛЬ - КТ2)")
    print("=" * 70)

    equipment = [
        {"equipment_id": 501, "serial_num": "VSH-2.3-0417", "model": "ВШ-2,3/400", "location": "Цех 3"},
        {"equipment_id": 502, "serial_num": "DD-110-2291", "model": "Датчик давления ДД-110", "location": "Цех 1"},
        {"equipment_id": 503, "serial_num": "AD-75-0042", "model": "Электродвигатель АД-75", "location": "Насосная"},
    ]

    experts = [
        {"user_id": 101, "full_name": "Петров П.П.", "role": "expert",
         "skills": ["компрессоры", "высокое_давление"], "rating": 5.0, "is_active": True},
        {"user_id": 102, "full_name": "Сидоров С.С.", "role": "expert",
         "skills": ["электроника", "автоматика", "датчики"], "rating": 4.8, "is_active": True},
        {"user_id": 103, "full_name": "Иванов И.И.", "role": "expert",
         "skills": ["механика", "электродвигатели"], "rating": 4.5, "is_active": False},
    ]

    # Операция 1: инженеры формируют карточки инцидентов
    incidents = [
        create_incident(1001, 501, 201, "Утечка масла и вибрация компрессора",
                        "Подтекание масла из-под крышки, вибрация выше нормы",
                        "компрессоры", ["компрессоры", "высокое_давление"], "high"),
        create_incident(1002, 502, 202, "Сбой датчика давления",
                        "Показания датчика скачут, ошибка на панели",
                        "электроника", ["электроника", "датчики"], "medium"),
        create_incident(1003, 503, 203, "Критический перегрев двигателя",
                        "Температура корпуса выше 110 °C, срабатывает защита",
                        "электродвигатели", ["электродвигатели", "механика"], "critical"),
        create_incident(1004, 501, 201, "Плановая проверка клапана",
                        "Требуется консультация по регулировке клапана",
                        "компрессоры", ["компрессоры"], "low"),
    ]

    print("\n--- 1. ОПЕРАЦИЯ 1: КАРТОЧКИ ИНЦИДЕНТОВ (входная очередь) ---")
    for inc in incidents:
        print(f"  [ID {inc['incident_id']}] {inc['priority_level']:<8} (вес {inc['weight']:>3}) - {inc['title']}")

    # Операция 2 / Алгоритм 2
    queue = prioritize_incidents_queue(incidents)
    print("\n--- 2. ОПЕРАЦИЯ 2 / АЛГОРИТМ 2: приоритизация очереди (Merge Sort, O(n log n)) ---")
    for rank, inc in enumerate(queue, 1):
        print(f"  Ранг {rank}: [ID {inc['incident_id']}] вес {inc['weight']:>3} -> {inc['title']}")

    # Операция 3 / Алгоритм 1: берём инциденты из очереди по порядку,
    # пока не найдём тот, для которого есть свободный эксперт («Эксперт найден?» — Нет -> ожидание)
    print("\n--- 3. ОПЕРАЦИЯ 3 / АЛГОРИТМ 1: подбор эксперта (O(n * m * s)) ---")
    incident = None
    expert = None
    score = 0
    for candidate in queue:
        expert, score = assign_best_expert(candidate["required_skills"], experts)
        if expert is None:
            print(f"  [ID {candidate['incident_id']}] эксперт не найден -> инцидент остаётся в очереди ожидания")
            continue
        incident = candidate
        break

    if incident is None:
        print("  Нет инцидентов, для которых доступен эксперт.")
        return

    assignment = create_assignment(4001, incident, expert, score)
    eq = find_by_id(equipment, "equipment_id", incident["equipment_id"])
    print(f"  Инцидент [ID {incident['incident_id']}] «{incident['title']}» ({eq['model']}, {eq['location']})")
    print(f"  Назначен эксперт: {expert['full_name']} (ID {expert['user_id']}), score = {score}")

    # Операция 4
    session = setup_webrtc_session(7001, assignment)
    print("\n--- 4. ОПЕРАЦИЯ 4: установление WebRTC P2P-сессии ---")
    print(f"  Сессия [ID {session['session_id']}], назначение {session['assignment_id']}, комната {session['room_id']}")
    print(f"  Качество связи: {session['quality']}")

    # Операция 5
    ar_mark = process_ar_annotation(9001, session["session_id"], "arrow", 0.25, 0.40, 0.60, 0.75, "#00FF00")
    print("\n--- 5. ОПЕРАЦИЯ 5: AR-аннотация эксперта ---")
    print(f"  Метка [ID {ar_mark['annotation_id']}]: '{ar_mark['tool_type']}' "
          f"({ar_mark['x1']}, {ar_mark['y1']}) -> ({ar_mark['x2']}, {ar_mark['y2']}), цвет {ar_mark['color']}")

    # Операция 6
    protocol = generate_consultation_protocol(
        3001, session, incident,
        "Устранена утечка масла на компрессоре ВШ-2,3/400. Рекомендация: заменить уплотнительное кольцо.",
    )
    print("\n--- 6. ОПЕРАЦИЯ 6: протокол заключения ---")
    print(f"  Протокол [ID {protocol['protocol_id']}] по сессии {protocol['session_id']}")
    print(f"  Заключение: {protocol['expert_notes']}")
    print(f"  Статус инцидента: {incident['status']}, закрыт: {protocol['closed_at']}")

    print("\n" + "=" * 70)
    print("  ВСЕ ОПЕРАЦИИ И АЛГОРИТМЫ ВЫПОЛНЕНЫ")
    print("=" * 70)


if __name__ == "__main__":
    main()
