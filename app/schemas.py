from datetime import datetime
from typing import Optional
from pydantic import BaseModel, ConfigDict
from app.models import UserRole, IncidentStatus

class UserBase(BaseModel):
    full_name: str
    email: str
    role: UserRole
    specialization: Optional[str] = None

class UserCreate(UserBase):
    pass

class UserResponse(UserBase):
    id: int
    model_config = ConfigDict(from_attributes=True)

class IncidentBase(BaseModel):
    title: str
    description: str
    equipment_code: str

class IncidentCreate(IncidentBase):
    created_by_id: int

class IncidentResponse(IncidentBase):
    id: int
    status: IncidentStatus
    created_at: datetime
    created_by_id: int
    assigned_expert_id: Optional[int] = None
    model_config = ConfigDict(from_attributes=True)

class AssignExpertRequest(BaseModel):
    expert_id: int
