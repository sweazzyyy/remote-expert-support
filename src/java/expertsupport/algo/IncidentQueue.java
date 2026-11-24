package expertsupport.algo;

import expertsupport.model.Incident;
import expertsupport.model.IncidentStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Очередь открытых инцидентов.
 * АЛГОРИТМ 2 (операция 2 «Приоритизировать очередь инцидентов»):
 * сортировка слиянием по убыванию веса приоритета, O(n log n), устойчивая.
 * В процедурной версии — функции prioritize_incidents_queue() и merge_incidents().
 */
public class IncidentQueue {
    private final List<Incident> incidents = new ArrayList<>();

    public void add(Incident incident) {
        incidents.add(incident);
    }

    public List<Incident> getIncidents() {
        return Collections.unmodifiableList(incidents);
    }

    /**
     * Новый список инцидентов, ожидающих назначения эксперта (статус CREATED),
     * отсортированный по убыванию веса; исходный порядок не меняется.
     * Назначенные и закрытые инциденты в результат не попадают.
     */
    public List<Incident> prioritize() {
        List<Incident> waiting = new ArrayList<>();
        for (Incident incident : incidents) {
            if (incident.getStatus() == IncidentStatus.CREATED) {
                waiting.add(incident);
            }
        }
        return mergeSort(waiting);
    }

    private static List<Incident> mergeSort(List<Incident> queue) {
        if (queue.size() <= 1) {                       // базовый случай рекурсии
            return queue;
        }
        int mid = queue.size() / 2;
        List<Incident> left = mergeSort(new ArrayList<>(queue.subList(0, mid)));
        List<Incident> right = mergeSort(new ArrayList<>(queue.subList(mid, queue.size())));
        return merge(left, right);
    }

    private static List<Incident> merge(List<Incident> left, List<Incident> right) {
        List<Incident> merged = new ArrayList<>(left.size() + right.size());
        int i = 0;
        int j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getWeight() >= right.get(j).getWeight()) {
                merged.add(left.get(i++));
            } else {
                merged.add(right.get(j++));
            }
        }
        while (i < left.size()) {
            merged.add(left.get(i++));
        }
        while (j < right.size()) {
            merged.add(right.get(j++));
        }
        return merged;
    }
}
