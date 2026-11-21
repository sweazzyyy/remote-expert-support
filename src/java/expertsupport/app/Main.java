package expertsupport.app;

import expertsupport.algo.ExpertSelector;
import expertsupport.algo.HashSetSkillMatcher;
import expertsupport.algo.IncidentQueue;
import expertsupport.algo.ListScanSkillMatcher;
import expertsupport.algo.MatchResult;
import expertsupport.algo.SkillMatcher;
import expertsupport.model.Engineer;
import expertsupport.model.Equipment;
import expertsupport.model.Expert;
import expertsupport.model.ExpertAssignment;
import expertsupport.model.Incident;
import expertsupport.model.PriorityLevel;
import expertsupport.model.Protocol;
import expertsupport.model.Skill;
import expertsupport.model.WebRtcSession;
import expertsupport.model.annotation.Annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ООП-версия процедурного модуля КТ2 (src/main.py).
 * Те же входные данные и тот же порядок операций, вывод совпадает построчно
 * (кроме заголовка и времени закрытия).
 *
 * Запуск: java ... expertsupport.app.Main [--matcher=list|hash]
 */
public final class Main {
    private static final String LINE = "=".repeat(70);

    private final Map<String, Skill> skills = new LinkedHashMap<>();
    private final Map<Integer, Equipment> equipment = new LinkedHashMap<>();
    private final Map<Integer, Engineer> engineers = new LinkedHashMap<>();
    private final List<Expert> experts = new ArrayList<>();
    private final IncidentQueue queue = new IncidentQueue();

    private Main() {
    }

    public static void main(String[] args) {
        SkillMatcher matcher = new ListScanSkillMatcher();
        for (String arg : args) {
            if (arg.equals("--matcher=hash")) {
                matcher = new HashSetSkillMatcher();
            } else if (!arg.equals("--matcher=list")) {
                System.err.println("Неизвестный параметр: " + arg + " (ожидается --matcher=list или --matcher=hash)");
                System.exit(2);
            }
        }
        Main app = new Main();
        app.loadData();
        app.run(matcher);
    }

    private Skill skill(String name) {
        return skills.computeIfAbsent(name, n -> new Skill(skills.size() + 1, n));
    }

    private List<Skill> skillList(String... names) {
        List<Skill> list = new ArrayList<>();
        for (String name : names) {
            list.add(skill(name));
        }
        return list;
    }

    private void loadData() {
        addEquipment(new Equipment(501, "VSH-2.3-0417", "ВШ-2,3/400", "Цех 3"));
        addEquipment(new Equipment(502, "DD-110-2291", "Датчик давления ДД-110", "Цех 1"));
        addEquipment(new Equipment(503, "AD-75-0042", "Электродвигатель АД-75", "Насосная"));

        experts.add(new Expert(101, "Петров П.П.", "petrov@example.org",
                skillList("компрессоры", "высокое_давление"), 5.0, true));
        experts.add(new Expert(102, "Сидоров С.С.", "sidorov@example.org",
                skillList("электроника", "автоматика", "датчики"), 4.8, true));
        experts.add(new Expert(103, "Иванов И.И.", "ivanov@example.org",
                skillList("механика", "электродвигатели"), 4.5, false));

        engineers.put(201, new Engineer(201, "Инженер цеха 3", "eng201@example.org"));
        engineers.put(202, new Engineer(202, "Инженер цеха 1", "eng202@example.org"));
        engineers.put(203, new Engineer(203, "Инженер насосной", "eng203@example.org"));

        // Операция 1: инженеры формируют карточки инцидентов
        queue.add(engineers.get(201).reportIncident(1001, equipment.get(501),
                "Утечка масла и вибрация компрессора", "Подтекание масла из-под крышки, вибрация выше нормы",
                "компрессоры", skillList("компрессоры", "высокое_давление"), PriorityLevel.fromCode("high")));
        queue.add(engineers.get(202).reportIncident(1002, equipment.get(502),
                "Сбой датчика давления", "Показания датчика скачут, ошибка на панели",
                "электроника", skillList("электроника", "датчики"), PriorityLevel.fromCode("medium")));
        queue.add(engineers.get(203).reportIncident(1003, equipment.get(503),
                "Критический перегрев двигателя", "Температура корпуса выше 110 °C, срабатывает защита",
                "электродвигатели", skillList("электродвигатели", "механика"), PriorityLevel.fromCode("critical")));
        queue.add(engineers.get(201).reportIncident(1004, equipment.get(501),
                "Плановая проверка клапана", "Требуется консультация по регулировке клапана",
                "компрессоры", skillList("компрессоры"), PriorityLevel.fromCode("low")));
    }

    private void addEquipment(Equipment e) {
        equipment.put(e.getId(), e);
    }

    private void run(SkillMatcher matcher) {
        System.out.println(LINE);
        System.out.println("  СИСТЕМА УДАЛЁННОЙ ЭКСПЕРТНОЙ ПОДДЕРЖКИ (ООП-МОДУЛЬ НА JAVA - КТ3)");
        System.out.println(LINE);

        System.out.println("\n--- 1. ОПЕРАЦИЯ 1: КАРТОЧКИ ИНЦИДЕНТОВ (входная очередь) ---");
        for (Incident inc : queue.getIncidents()) {
            System.out.printf("  [ID %d] %-8s (вес %3d) - %s%n",
                    inc.getId(), inc.getPriority().getCode(), inc.getWeight(), inc.getTitle());
        }

        // Операция 2 / Алгоритм 2
        List<Incident> prioritized = queue.prioritize();
        System.out.println("\n--- 2. ОПЕРАЦИЯ 2 / АЛГОРИТМ 2: приоритизация очереди (Merge Sort, O(n log n)) ---");
        int rank = 1;
        for (Incident inc : prioritized) {
            System.out.printf("  Ранг %d: [ID %d] вес %3d -> %s%n", rank++, inc.getId(), inc.getWeight(),
                    inc.getTitle());
        }

        // Операция 3 / Алгоритм 1
        ExpertSelector selector = new ExpertSelector(experts, matcher);
        System.out.println("\n--- 3. ОПЕРАЦИЯ 3 / АЛГОРИТМ 1: подбор эксперта (" + matcher.complexity() + ") ---");
        Incident incident = null;
        MatchResult match = null;
        for (Incident candidate : prioritized) {
            Optional<MatchResult> found = selector.findBestExpert(candidate.getRequiredSkills());
            if (found.isEmpty()) {
                System.out.printf("  [ID %d] эксперт не найден -> инцидент остаётся в очереди ожидания%n",
                        candidate.getId());
                continue;
            }
            incident = candidate;
            match = found.get();
            break;
        }
        if (incident == null) {
            System.out.println("  Нет инцидентов, для которых доступен эксперт.");
            return;
        }

        ExpertAssignment assignment = incident.assignTo(4001, match.getExpert(), match.getScore());
        Equipment eq = incident.getEquipment();
        System.out.printf("  Инцидент [ID %d] «%s» (%s, %s)%n", incident.getId(), incident.getTitle(),
                eq.getModel(), eq.getLocation());
        System.out.printf("  Назначен эксперт: %s (ID %d), score = %s%n", match.getExpert().getFullName(),
                match.getExpert().getId(), match.getScore());

        // Операция 4
        WebRtcSession session = assignment.openSession(7001);
        System.out.println("\n--- 4. ОПЕРАЦИЯ 4: установление WebRTC P2P-сессии ---");
        System.out.printf("  Сессия [ID %d], назначение %d, комната %s%n", session.getId(), assignment.getId(),
                session.getRoomId());
        System.out.println("  Качество связи: " + session.getQuality());

        // Операция 5
        Annotation mark = session.addAnnotation(9001, "arrow", 0.25, 0.40, 0.60, 0.75, "#00FF00");
        System.out.println("\n--- 5. ОПЕРАЦИЯ 5: AR-аннотация эксперта ---");
        System.out.printf("  Метка [ID %d]: '%s' %s, цвет %s%n", mark.getId(), mark.getToolType(),
                mark.describeGeometry(), mark.getColor());

        // Операция 6
        Protocol protocol = session.close(3001,
                "Устранена утечка масла на компрессоре ВШ-2,3/400. Рекомендация: заменить уплотнительное кольцо.");
        System.out.println("\n--- 6. ОПЕРАЦИЯ 6: протокол заключения ---");
        System.out.printf("  Протокол [ID %d] по сессии %d%n", protocol.getId(), session.getId());
        System.out.println("  Заключение: " + protocol.getExpertNotes());
        System.out.println("  Статус инцидента: " + incident.getStatus() + ", закрыт: " + protocol.getClosedAt());

        System.out.println("\n" + LINE);
        System.out.println("  ВСЕ ОПЕРАЦИИ И АЛГОРИТМЫ ВЫПОЛНЕНЫ");
        System.out.println(LINE);
    }
}
