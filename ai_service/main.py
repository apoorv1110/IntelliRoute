from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI(title="IntelliRoute AI Service", version="0.1.0")


class QueryRequest(BaseModel):
    query: str


@app.post("/predict_complexity")
async def predict_complexity(payload: QueryRequest):
    """
    Lightweight heuristic placeholder until a transformer model is wired.
    """
    text = payload.query.lower()
    keywords = ["outage", "critical", "latency", "security", "data loss", "p1"]
    score = 1.0 + min(len(text) / 300, 3.0)
    if any(k in text for k in keywords):
        score += 1.2
    if "architecture" in text or "refactor" in text:
        score += 0.8
    if "simple" in text or "typo" in text:
        score -= 0.5

    score = max(1.0, min(score, 5.0))
    return {"complexity_score": round(score, 2)}

