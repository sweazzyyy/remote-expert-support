from fastapi import FastAPI
from app.database import engine, Base
from app.routers import incidents, websocket_signaling

# Автоматическое создание таблиц базы данных при старте
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Система удалённой экспертной поддержки (Remote Expert Support API)",
    description="Backend-сервис для автоматизации обработки заявок инженеров, назначения экспертов и видеосвязи с AR-подсказками.",
    version="1.0.0"
)

app.include_router(incidents.router)
app.include_router(websocket_signaling.router)

@app.get("/", tags=["Health"])
def root_health():
    return {
        "status": "online",
        "service": "Remote Expert Support Backend",
        "docs_url": "/docs"
    }
