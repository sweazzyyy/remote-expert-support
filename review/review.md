# Взаимное рецензирование кода (КТ3)

## 1. Кого рецензировали

- **Автор:** Засковец А. С., группа ИКБО-31-24
- **Репозиторий:** https://github.com/loolize/defect-vision-quality-control
- **Коммит, с которого взят код:** [`8f09c82`](https://github.com/loolize/defect-vision-quality-control/commit/8f09c82a2e1ad865826c59abf1f515195a686632) от 24.11.2026 («.drawio диаграмма исправлена»)
- **Что рецензировалось:** ООП-версия на Java `src/java/src/*.java` (16 классов), `src/java/README.md`, `src/java/pom.xml`, диаграмма классов `models/classes.drawio` / `classes.png`; для сверки алгоритмов — процедурная версия `src/main.py`.
- **Способ рецензирования:** только в файле `review/review.md`

## 2. Что проверяли

| Критерий | Результат |
|---|---|
| Соответствие диаграмме классов | Классы, поля и методы совпадают с кодом. Часть ассоциаций на диаграмме в коде не выражена ссылками (замечание 3) |
| Наследование | `BaseInspection` → `AutomaticInspection`, `ManualInspection` — осмысленное, подклассы по-разному реализуют `inspect()`. `SpecialistReview` наследуется без смысла (замечание 1) |
| Интерфейсы | `InspectionMethod` с реализациями `AutomaticInspection` и `ManualInspection`, выбор в `Main` через тип интерфейса — требование выполнено |
| Коллекции | `List`, `ArrayList`, `Map` (`LinkedHashMap`); массивов для хранения сущностей нет — требование выполнено |
| Инкапсуляция | Поля закрыты, коллекции отдаются копиями (`List.copyOf`) |
| Совпадение алгоритмов с процедурной версией | По коду пороги совпадают с `src/main.py`: яркость 50–230, резкость ≥ 15, порог пикселя 150, доли 0,06 и 0,18 |
| Читаемость | Код читается, но много лишних пустых строк и строковых констант-статусов, дублирующихся по классам (замечание 2) |
| Сборка и запуск по README | Выполнено точно по командам README (Apache Maven 3.10.2, JDK 21.0.12): `mvn -f src/java/pom.xml compile` — BUILD SUCCESS, 16 файлов; `mvn -q -f src/java/pom.xml exec:java -Dexec.mainClass=Main` с входными данными из примера README (стандартное изображение, подтверждение «да», разбор специалистом «нет»). Вывод совпадает с ожидаемым: 18 ячеек, заполнено 16, пустые 7 и 10, неопределённых нет, предварительное и итоговое заключение «Обнаружена пустая ячейка». После запуска в рабочей копии изменился отслеживаемый файл `src/inspection_java.json` (замечание 4) |

## 3. Замечания

**#1. Наследование `SpecialistReview` от `BaseInspection` без смысла**
`src/java/src/SpecialistReview.java`, строки 3 и 45–48; `src/java/src/Main.java`, строки 82–84.
`SpecialistReview` не является способом проверки, но наследует `BaseInspection` и через него реализует `InspectionMethod`. Метод `inspect()` нигде не вызывается (`Main` вызывает `checkReview()`), а если вызвать, он передаст `inspection.getFinalStatus()`, который до `confirm()` равен `null`. Наследование используется только ради `printOptions()` и `getStatusByChoice()`.
**Предложение:** убрать `extends BaseInspection` и метод `inspect()`; общие `printOptions()`/`getStatusByChoice()` вынести в отдельный класс (например, `StatusMenu`) и использовать через композицию — в `BaseInspection` и `SpecialistReview`.

**#2. Статусы и состояния ячеек — строковые литералы, продублированные в нескольких классах**
`src/java/src/CellAnalyzer.java`, строки 31–36; `src/java/src/CellResult.java`, строки 28–34; `src/java/src/Inspection.java`, строки 78–85; `src/java/src/BaseInspection.java`, строки 12–25; `src/java/src/ManualInspection.java`, строка 7.
Строки `"empty"`, `"filled"`, `"uncertain"`, `"Полный"`, `"Обнаружена пустая ячейка"`, `"Требуется повторная проверка"` написаны вручную в разных файлах. Опечатка в одном месте не будет замечена компилятором, и, например, `CellResult.isEmpty()` молча начнёт возвращать `false`.
**Предложение:** завести перечисления `CellState { EMPTY, FILLED, UNCERTAIN }` и `InspectionStatus` (с русским названием в поле), хранить в `CellResult` и `Inspection` их, а для JSON задать имя через `@SerializedName`.

**#3. Ассоциации на диаграмме классов не выражены в коде**
`models/classes.drawio` — связи `Series`–`Blister`, `Blister`–`Template`, `Blister`–`InspectionImage`, `InspectionImage`–`Inspection`, `Inspection`–`Operator`, `TemplateCell`–`CellResult`, `Template`–`TemplateCell`.
В коде эти классы хранят только целочисленные идентификаторы (`Blister.seriesId`, `Inspection.operatorId`, `CellResult.cellId` и т. д.), ссылок на объекты нет, а у `Template` нет списка ячеек — ячейки лежат в `JsonStorage`. На диаграмме классов это выглядит как навигируемые ассоциации, то есть «связи» диаграммы не совпадают с кодом (требование памятки «те же классы, поля и связи»).
**Предложение:** либо заменить идентификаторы ссылками на объекты (`private final Operator operator` и т. п.), либо на диаграмме оставить только связи, которые реально есть в коде (поля-ссылки и коллекции), а связи по идентификаторам показать как зависимости или пояснить в отчёте.

**#4. Файлы результатов работы программы лежат в репозитории**
`src/inspection_java.json`, `src/images/inspection_image_java.png` (а также `src/inspection.json`, `src/images/inspection_image.png` от процедурной версии); `src/java/src/Main.java`, строки 89–93 и 172.
Программа при каждом запуске перезаписывает эти отслеживаемые файлы. При проверке по README так и произошло: после одного запуска `git status` показал изменённый `src/inspection_java.json` (новые даты `created_at`, `confirmed_at` и имя оператора). Такие изменения легко случайно закоммитить.
**Предложение:** добавить выходные файлы в `.gitignore` и удалить их из индекса (`git rm --cached`), а пример результата положить отдельным файлом, например `src/examples/inspection_java_example.json`.

**#5. Попиксельный доступ к `Mat` в `CellAnalyzer.analyzeCell`**
`src/java/src/CellAnalyzer.java`, строки 16–25.
`image.get(y, x)` для каждого пикселя делает отдельный вызов через JNI и создаёт новый массив `double[]` — на больших изображениях это очень медленно.
**Предложение:** брать ячейку через `image.submat(...)`, применять `Imgproc.threshold(..., 150, 255, THRESH_BINARY_INV)` и считать тёмные пиксели `Core.countNonZero()`; либо читать строку целиком в `byte[]` через `image.get(row, col, buffer)`. Результат при этом не изменится.

**#6. Утечка нативной памяти при повторной загрузке изображения**
`src/java/src/InspectionImage.java`, строка 32; `src/java/src/Main.java`, строки 101–119.
При каждой попытке `load()` записывает в поле `image` новый `Mat`, не освобождая предыдущий. Объекты `Mat` хранят данные вне кучи Java, сборщик мусора освобождает их непредсказуемо.
**Предложение:** перед повторной загрузкой вызывать `image.release()`, если поле не `null`.

**#7. Мелкие замечания по оформлению**
- `src/java/src/AutomaticInspection.java`, строка 4 и `src/java/src/ManualInspection.java`, строка 3: `implements InspectionMethod` избыточно — интерфейс уже реализует `BaseInspection`.
- `src/java/src/CellAnalyzer.java`, строки 6–8: пороги объявлены как поля экземпляра; это константы — лучше `private static final int PIXEL_THRESHOLD = 150` и т. д.
- `src/java/README.md`, раздел «Входные данные»: опечатка «при запросе пути к файл» → «к файлу».

## 4. Что исправлено по замечаниям к моему коду

Рецензент: Засковец А. С., группа ИКБО-31-24. Рецензия на коммит [`afa7b20`](https://github.com/sweazzyyy/remote-expert-support/commit/afa7b20cd75aa1e2b5fbcadc4c8817b008835f65), способ — замечания в файле. Получено 4 замечания, все исправлены, каждое отдельным коммитом. После исправлений вывод Java-версии по-прежнему совпадает с процедурной версией на Python (проверено построчным `diff`), а каждая исправленная ситуация проверена отдельным запуском.

| № | Полученное замечание (файл, место, текст) | Результат | Коммит |
|---|---|---|---|
| 1 | `model/Incident.java`, метод `assignTo()`: статус меняется на `ASSIGNED` без проверки, поэтому повторный вызов после закрытия сеанса переводит инцидент из `RESOLVED` обратно в `ASSIGNED`. Предложено проверять статус `CREATED` и иначе выбрасывать `IllegalStateException` | Исправлено: назначение возможно только из статуса `CREATED`, иначе `IllegalStateException`; статус закрытого инцидента не меняется | [`b9a08d6`](https://github.com/sweazzyyy/remote-expert-support/commit/b9a08d6) |
| 2 | `model/ExpertAssignment.java`, метод `openSession()`: при каждом вызове создаётся новый `WebRtcSession`, что противоречит кратности `0..1` на диаграмме. Предложено хранить сеанс в закрытом поле, запретить повторное создание и показать поле на диаграмме | Исправлено: добавлено поле `session` и метод `getSession()`, повторный `openSession()` выбрасывает `IllegalStateException`; на диаграмме классов добавлено поле `session`, связь `ExpertAssignment` — `WebRtcSession` стала двунаправленной с кратностью `1` — `0..1` | [`6f0001c`](https://github.com/sweazzyyy/remote-expert-support/commit/6f0001c) |
| 3 | `algo/IncidentQueue.java`, метод `prioritize()`: сортируются все инциденты, включая закрытые, и они могут быть выбраны повторно. Предложено сортировать только инциденты в статусе `CREATED` | Исправлено: в сортировку слиянием передаются только инциденты в статусе `CREATED` | [`0b474f8`](https://github.com/sweazzyyy/remote-expert-support/commit/0b474f8) |
| 4 | `model/Incident.java`, конструктор; `ListScanSkillMatcher` и `HashSetSkillMatcher`, `countMatches()`: повторяющиеся навыки считаются дважды и завышают оценку эксперта, хотя в модели данных пара (`incident_id`, `skill_id`) уникальна. Предложено убирать повторы через `LinkedHashSet` | Исправлено: в конструкторе повторы удаляются через `LinkedHashSet` с сохранением порядка, обе реализации `SkillMatcher` теперь дают одно совпадение на навык | [`c8fa96b`](https://github.com/sweazzyyy/remote-expert-support/commit/c8fa96b) |
