# ООП-версия на Java (КТ3)

Объектно-ориентированная реализация того же процесса, что процедурная версия КТ2 на **Python** (`src/main.py`): те же входные данные, те же 6 операций и 2 алгоритма, тот же вывод.

## Требования
- **JDK 21** (проверено на Eclipse Temurin 21.0.12). Подойдёт JDK 17+.
- Сборка — `javac`, без Maven/Gradle и внешних библиотек.
- Данные хранятся в памяти (List / Map), БД подключается на КТ4.

> На этом компьютере первым в `PATH` стоит `java` из Java 8, а `javac` — из JDK 21. Скомпилированные классы Java 8 запустить не сможет, поэтому `java` вызывается из JDK 21 явно (переменная `JDK` ниже).

## Сборка и запуск

Все команды — из папки `src/java`.

**Git Bash / Linux / macOS**
```bash
JDK="/c/Program Files/Eclipse Adoptium/jdk-21.0.12.101-hotspot/bin"   # или путь к своему JDK 17+
"$JDK/javac" -encoding UTF-8 -d out $(find expertsupport -name "*.java")
"$JDK/java" -Dstdout.encoding=UTF-8 -cp out expertsupport.app.Main
```

**PowerShell**
```powershell
$JDK = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin"
& "$JDK\javac.exe" -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java expertsupport).FullName
& "$JDK\java.exe" -Dstdout.encoding=UTF-8 -cp out expertsupport.app.Main
```

Параметр выбора правила подсчёта навыков (интерфейс `SkillMatcher`):
- `--matcher=list` (по умолчанию) — линейный поиск по списку, как в Python, O(n·m·s);
- `--matcher=hash` — множество `HashSet`, O(n·(m+s)). Результат тот же.

Папка `out/` с `.class`-файлами в репозиторий не попадает (`.gitignore`).

## Входные данные
Задаются в `expertsupport/app/Main.java` (метод `loadData`) и совпадают с `src/main.py`:
- 3 единицы оборудования (501–503), 3 эксперта (101 и 102 активны, 103 неактивен);
- 4 инцидента: 1001 high, 1002 medium, 1003 critical (нужны «электродвигатели», «механика» — их знает только неактивный эксперт), 1004 low.

## Ожидаемый вывод
```
--- 2. ОПЕРАЦИЯ 2 / АЛГОРИТМ 2: приоритизация очереди (Merge Sort, O(n log n)) ---
  Ранг 1: [ID 1003] вес 100 -> Критический перегрев двигателя
  Ранг 2: [ID 1001] вес  75 -> Утечка масла и вибрация компрессора
  Ранг 3: [ID 1002] вес  50 -> Сбой датчика давления
  Ранг 4: [ID 1004] вес  25 -> Плановая проверка клапана

--- 3. ОПЕРАЦИЯ 3 / АЛГОРИТМ 1: подбор эксперта (O(n * m * s)) ---
  [ID 1003] эксперт не найден -> инцидент остаётся в очереди ожидания
  Инцидент [ID 1001] «Утечка масла и вибрация компрессора» (ВШ-2,3/400, Цех 3)
  Назначен эксперт: Петров П.П. (ID 101), score = 205.0

--- 4. ОПЕРАЦИЯ 4: установление WebRTC P2P-сессии ---
  Сессия [ID 7001], назначение 4001, комната room_webrtc_1001_7001
...
  Метка [ID 9001]: 'arrow' (0.25, 0.4) -> (0.6, 0.75), цвет #00FF00
...
  Статус инцидента: resolved, закрыт: <текущее время>
```

## Сверка с процедурной версией (Git Bash, из корня репозитория)
```bash
JDK="/c/Program Files/Eclipse Adoptium/jdk-21.0.12.101-hotspot/bin"
(cd src/java && "$JDK/javac" -encoding UTF-8 -d out $(find expertsupport -name "*.java"))
norm() { grep -v "СИСТЕМА УДАЛЁННОЙ" | sed 's/закрыт: .*/закрыт: <time>/' | tr -d '\r'; }
diff <(PYTHONIOENCODING=utf-8 python src/main.py | norm) \
     <("$JDK/java" -Dstdout.encoding=UTF-8 -cp src/java/out expertsupport.app.Main | norm) && echo "Вывод совпадает"
```
Различаются только заголовок (название модуля) и время закрытия протокола.

## Структура
| Пакет | Классы | Что соответствует в КТ2 |
|---|---|---|
| `expertsupport.model` | `User` (абстр.), `Engineer`, `Expert`, `Skill`, `Equipment`, `PriorityLevel`, `IncidentStatus`, `Incident`, `ExpertAssignment`, `WebRtcSession`, `Protocol` | сущности модели данных |
| `expertsupport.model.annotation` | `Annotation` (абстр.), `ArrowAnnotation`, `RectAnnotation`, `LineAnnotation` | сущность ANNOTATION, `process_ar_annotation()` |
| `expertsupport.algo` | `IncidentQueue` (Алгоритм 2), `ExpertSelector` (Алгоритм 1), `SkillMatcher` + `ListScanSkillMatcher`, `HashSetSkillMatcher`, `MatchResult` | `prioritize_incidents_queue()`, `assign_best_expert()` |
| `expertsupport.app` | `Main` | `main()` |

Тестов нет. Корректность проверяется сверкой вывода с Python-версией (см. выше).
