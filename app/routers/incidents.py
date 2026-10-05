from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import Incident, User, IncidentStatus, UserRole
from app import schemas

router = APIRouter(prefix="/api/incidents", tags=["Incidents"])

@router.post("/", response_model=schemas.IncidentResponse, status_code=status.HTTP_201_CREATED)
def create_incident(incident: schemas.IncidentCreate, db: Session = Depends(get_db)):
    creator = db.query(User).filter(User.id == incident.created_by_id).first()
    if not creator:
        raise HTTPException(status_code=404, detail="Пользователь-создатель не найден")
    
    db_incident = Incident(
        title=incident.title,
        description=incident.description,
        equipment_code=incident.equipment_code,
        created_by_id=incident.created_by_id,
        status=IncidentStatus.CREATED
    )
    db.add(db_incident)
    db.commit()
    db.refresh(db_incident)
    return db_incident

@router.get("/", response_model=List[schemas.IncidentResponse])
def list_incidents(skip: int = 0, limit: int = 20, db: Session = Depends(get_db)):
    return db.query(Incident).offset(skip).limit(limit).all()

@router.get("/{incident_id}", response_model=schemas.IncidentResponse)
def get_incident(incident_id: int, db: Session = Depends(get_db)):
    incident = db.query(Incident).filter(Incident.id == incident_id).first()
    if not incident:
        raise HTTPException(status_code=404, detail="Инцидент не найден")
    return incident

@router.put("/{incident_id}/assign", response_model=schemas.IncidentResponse)
def assign_expert(incident_id: int, payload: schemas.AssignExpertRequest, db: Session = Depends(get_db)):
    incident = db.query(Incident).filter(Incident.id == incident_id).first()
    if not incident:
        raise HTTPException(status_code=404, detail="Инцидент не найден")
    
    expert = db.query(User).filter(User.id == payload.expert_id, User.role == UserRole.EXPERT).first()
    if not expert:
        raise HTTPException(status_code=400, detail="Указанный пользователь не является зарегистрированным экспертом")
    
    incident.assigned_expert_id = payload.expert_id
    incident.status = IncidentStatus.ASSIGNED
    db.commit()
    db.refresh(incident)
    return incident
