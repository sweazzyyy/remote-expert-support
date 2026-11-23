# Система удалённой экспертной поддержки (Remote Expert Support System)

Учебный проект по практике. Система предназначена для организации дистанционного экспертного сопровождения технического персонала при обслуживании и ремонте сложного технологического оборудования.

## Стек технологий
* **Python 3.12**
* **FastAPI** — асинхронный веб-фреймворк для REST API и WebSockets
* **SQLAlchemy** — ORM для работы с базами данных (PostgreSQL / SQLite)
* **Pydantic v2** — валидация данных и схем
* **WebSockets / WebRTC (aiortc)** — передача видеопотока и сигналов AR-аннотаций в реальном времени
* **Uvicorn** — ASGI сервер
* **Java 21 (JDK 21, Eclipse Temurin)** — ООП-версия процедурного модуля (КТ3), сборка `javac` без внешних библиотек; подходит JDK 17+

## Структура проекта
```
remote_expert_support/
├── README.md
├── .gitignore
├── requirements.txt
├── vcs/
│   ├── repo_url.txt
│   └── git_log.txt
├── models/                       # Исходники draw.io + PNG (BPMN, ER 3НФ, блок-схемы, диаграмма классов UML)
├── docs/
│   └── report_section6.md        # Текст раздела 6 отчёта КТ2
├── src/
│   ├── main.py                   # Процедурный модуль КТ2 (python src/main.py)
│   ├── README.md
│   └── java/                     # ООП-версия на Java 21 (КТ3)
│       ├── README.md             # Сборка и запуск, сверка с процедурной версией
│       └── expertsupport/        # Пакеты model, model.annotation, algo, app
└── app/
    ├── __init__.py
    ├── main.py                   # Точка входа приложения
    ├── database.py               # Настройки и сессия БД
    ├── models.py                 # ORM модели (User, Incident)
    ├── schemas.py                # Pydantic схемы
    └── routers/
        ├── __init__.py
        ├── incidents.py          # REST API эндпоинты инцидентов
        └── websocket_signaling.py# WebSocket signaling для WebRTC и AR
```

## Быстрый запуск

1. **Создание и активация виртуального окружения:**
   ```bash
   python -m venv venv
   # Linux / macOS:
   source venv/bin/activate
   # Windows:
   venv\Scripts\activate
   ```

2. **Установка зависимостей:**
   ```bash
   pip install -r requirements.txt
   ```

3. **Запуск сервера:**
   ```bash
   uvicorn app.main:app --reload
   ```

4. **Документация API:**
   После запуска перейдите в браузере по адресу: [http://127.0.0.1:8000/docs](http://127.0.0.1:8000/docs)

## ООП-версия на Java (КТ3)

Требуется **JDK 21** (проверено на Eclipse Temurin 21.0.12, подойдёт JDK 17+). Из папки `src/java`:

```bash
javac -encoding UTF-8 -d out $(find expertsupport -name "*.java")
java -Dstdout.encoding=UTF-8 -cp out expertsupport.app.Main
```

Если в `PATH` первым стоит `java` из Java 8, вызывайте `java` и `javac` из каталога JDK 21 явно. Подробности и команды для PowerShell — в [src/java/README.md](src/java/README.md).

