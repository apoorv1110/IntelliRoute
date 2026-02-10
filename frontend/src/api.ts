import axios from 'axios';

export type Designation = 'JUNIOR' | 'MID' | 'SENIOR' | 'TECH_LEAD';
export type Priority = 'P1' | 'P2' | 'P3';
export type QueryStatus = 'PENDING' | 'ASSIGNED' | 'RESOLVED' | 'ESCALATED';
export type AssignmentStatus = 'ACTIVE' | 'COMPLETED' | 'ESCALATED';

export interface Engineer {
  id: string;
  name: string;
  designation: Designation;
  capacity: number;
  currentLoad: number;
  available: boolean;
  skills: string[];
  timezone?: string | null;
}

export interface SupportQuery {
  id: string;
  description: string;
  complexityScore: number | null;
  status: QueryStatus;
  priority: Priority;
  tags: string[];
  domain?: string | null;
}

export interface Assignment {
  id: string;
  engineerId: string;
  queryId: string;
  allocationPercent: number;
  status: AssignmentStatus;
  assignedAt: string;
}

const api = axios.create({
  baseURL: '/api'
});

export async function fetchEngineers(): Promise<Engineer[]> {
  const res = await api.get<Engineer[]>('/engineers');
  return res.data;
}

export async function createEngineer(payload: {
  name: string;
  designation: Designation;
  capacity: number;
  skills: string[];
  available: boolean;
  timezone?: string;
}): Promise<Engineer> {
  const res = await api.post<Engineer>('/engineers', payload);
  return res.data;
}

export async function fetchQueries(): Promise<SupportQuery[]> {
  const res = await api.get<SupportQuery[]>('/queries');
  return res.data;
}

export async function createQuery(payload: {
  description: string;
  priority: Priority;
  tags: string[];
  domain?: string;
}): Promise<SupportQuery> {
  const res = await api.post<SupportQuery>('/queries', payload);
  return res.data;
}

export async function fetchAssignments(): Promise<Assignment[]> {
  const res = await api.get<Assignment[]>('/assignments');
  return res.data;
}

export async function runAssignmentCycle(): Promise<void> {
  await api.post('/assignments/run');
}

export async function completeAssignment(id: string): Promise<Assignment> {
  const res = await api.put<Assignment>(`/assignments/${id}/complete`);
  return res.data;
}


