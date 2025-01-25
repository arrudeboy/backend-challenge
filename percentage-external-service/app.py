from fastapi import FastAPI

from pydantic import BaseModel


class Output(BaseModel):
    percentage: float


app = FastAPI()


@app.get("/", response_model=Output)
def percentage():
    percentage = 30 # fixed value for testing purpose
    return Output(percentage=percentage)
