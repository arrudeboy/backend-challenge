from fastapi import FastAPI

from pydantic import BaseModel

db = {"percentage": 10}

class PercentageDTO(BaseModel):
    percentage: float


app = FastAPI()


@app.get("/", response_model=PercentageDTO)
def get_percentage():
    return PercentageDTO(percentage=db["percentage"])


@app.put("/", response_model=PercentageDTO)
def update_percentage(percentage_dto: PercentageDTO):
    db["percentage"] = percentage_dto.percentage
    return PercentageDTO(percentage=db["percentage"])
