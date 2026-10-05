import enum
from datetime import datetime
from sqlalchemy import Column, Integer, String, Text, DateTime, Enum as SQLEnum, ForeignKey
from sqlalchemy.orm import relationship
from app.database import Base

class UserRole(str, enum.Enum):
    ENGINEER = "engineer"
    EXPERT = "expert"
    DISPATCHER = "dispatcher"
    ADMIN = "admin"

class IncidentStatus(str, enum.Enum):
    CREATED = "created"
    ASSIGNED = "assigned"
    IN_PROGRESS = "in_progress"
    RESOLVED = "resolved"
    CLOSED = "closed"

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    full_name = Column(String(100), nullable=False)
    email = Column(String(100), unique=True, index=True, nullable=False)
    role = Column(SQLEnum(UserRole), default=UserRole.ENGINEER, nullable=False)
    specialization = Column(String(100), nullable=True)

    incidents_created = relationship("Incident", foreign_keys="Incident.created_by_id", back_populates="creator")
    incidents_assigned = relationship("Incident", foreign_keys="Incident.assigned_expert_id", back_populates="expert")

class Incident(Base):
    __tablename__ = "incidents"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(200), nullable=False)
    description = Column(Text, nullable=False)
    equipment_code = Column(String(50), nullable=False)
    status = Column(SQLEnum(IncidentStatus), default=IncidentStatus.CREATED, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    created_by_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    assigned_expert_id = Column(Integer, ForeignKey("users.id"), nullable=True)

    creator = relationship("User", foreign_keys=[created_by_id], back_populates="incidents_created")
    expert = relationship("User", foreign_keys=[assigned_expert_id], back_populates="incidents_assigned")
