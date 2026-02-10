import React, { useEffect, useState } from 'react';
import {
  Assignment,
  AssignmentStatus,
  Designation,
  Engineer,
  Priority,
  SupportQuery,
  completeAssignment,
  createEngineer,
  createQuery,
  fetchAssignments,
  fetchEngineers,
  fetchQueries,
  runAssignmentCycle
} from './api';

type Tab = 'engineers' | 'queries' | 'assignments';

const designationOptions: { value: Designation; label: string }[] = [
  { value: 'JUNIOR', label: 'Junior SE' },
  { value: 'MID', label: 'Software Engineer' },
  { value: 'SENIOR', label: 'Senior SE' },
  { value: 'TECH_LEAD', label: 'Tech Lead' }
];

const priorityOptions: { value: Priority; label: string }[] = [
  { value: 'P1', label: 'P1 - Critical' },
  { value: 'P2', label: 'P2 - High' },
  { value: 'P3', label: 'P3 - Normal' }
];

function App() {
  const [tab, setTab] = useState<Tab>('engineers');
  const [engineers, setEngineers] = useState<Engineer[]>([]);
  const [queries, setQueries] = useState<SupportQuery[]>([]);
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [newEngineer, setNewEngineer] = useState({
    name: '',
    designation: 'JUNIOR' as Designation,
    capacity: 3,
    skills: ''
  });

  const [newQuery, setNewQuery] = useState({
    description: '',
    priority: 'P3' as Priority,
    tags: '',
    domain: ''
  });

  async function loadAll() {
    try {
      setLoading(true);
      setError(null);
      const [eng, q, asg] = await Promise.all([
        fetchEngineers(),
        fetchQueries(),
        fetchAssignments()
      ]);
      setEngineers(eng);
      setQueries(q);
      setAssignments(asg);
    } catch (e: any) {
      setError(e?.message ?? 'Failed to load data');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAll().catch(() => undefined);
  }, []);

  const handleCreateEngineer = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      const skills = newEngineer.skills
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean);
      const created = await createEngineer({
        name: newEngineer.name,
        designation: newEngineer.designation,
        capacity: newEngineer.capacity,
        skills,
        available: true
      });
      setEngineers((prev) => [...prev, created]);
      setNewEngineer({ name: '', designation: 'JUNIOR', capacity: 3, skills: '' });
    } catch (e: any) {
      setError(e?.message ?? 'Failed to create engineer');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateQuery = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      const tags = newQuery.tags
        .split(',')
        .map((t) => t.trim())
        .filter(Boolean);
      const created = await createQuery({
        description: newQuery.description,
        priority: newQuery.priority,
        tags,
        domain: newQuery.domain || undefined
      });
      setQueries((prev) => [...prev, created]);
      setNewQuery({ description: '', priority: 'P3', tags: '', domain: '' });
    } catch (e: any) {
      setError(e?.message ?? 'Failed to create query');
    } finally {
      setLoading(false);
    }
  };

  const handleRunAssignments = async () => {
    try {
      setLoading(true);
      await runAssignmentCycle();
      const [updatedQueries, updatedAssignments] = await Promise.all([
        fetchQueries(),
        fetchAssignments()
      ]);
      setQueries(updatedQueries);
      setAssignments(updatedAssignments);
    } catch (e: any) {
      setError(e?.message ?? 'Failed to run assignment cycle');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleComplete = async (assignment: Assignment) => {
    if (assignment.status === 'COMPLETED') {
      return;
    }
    try {
      setLoading(true);
      const updated = await completeAssignment(assignment.id);
      setAssignments((prev) =>
        prev.map((a) => (a.id === updated.id ? updated : a))
      );
      const updatedQueries = await fetchQueries();
      setQueries(updatedQueries);
      const updatedEngineers = await fetchEngineers();
      setEngineers(updatedEngineers);
    } catch (e: any) {
      setError(e?.message ?? 'Failed to complete assignment');
    } finally {
      setLoading(false);
    }
  };

  const engineerNameById = (id: string) =>
    engineers.find((e) => e.id === id)?.name ?? 'Unknown';
  const queryById = (id: string) => queries.find((q) => q.id === id);

  const renderTab = () => {
    switch (tab) {
      case 'engineers':
        return (
          <div className="grid gap-6 md:grid-cols-[minmax(0,1.2fr)_minmax(0,1.8fr)]">
            <form
              onSubmit={handleCreateEngineer}
              className="space-y-4 rounded-xl bg-slate-900/60 p-4 border border-slate-800"
            >
              <h2 className="text-lg font-semibold mb-1">Register Engineer</h2>
              <p className="text-xs text-slate-400 mb-2">
                Add engineers with designation, capacity and skills. Capacity controls how many active queries they can own.
              </p>
              <div>
                <label className="block text-sm mb-1">Name</label>
                <input
                  className="w-full rounded-md bg-slate-800 border border-slate-700 px-3 py-2 text-sm"
                  value={newEngineer.name}
                  onChange={(e) =>
                    setNewEngineer((s) => ({ ...s, name: e.target.value }))
                  }
                  required
                />
              </div>
              <div className="flex gap-3">
                <div className="flex-1">
                  <label className="block text-sm mb-1">Designation</label>
                  <select
                    className="w-full rounded-md bg-slate-800 border border-slate-700 px-3 py-2 text-sm"
                    value={newEngineer.designation}
                    onChange={(e) =>
                      setNewEngineer((s) => ({
                        ...s,
                        designation: e.target.value as Designation
                      }))
                    }
                  >
                    {designationOptions.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="w-28">
                  <label className="block text-sm mb-1">Capacity</label>
                  <input
                    type="number"
                    min={1}
                    className="w-full rounded-md bg-slate-800 border border-slate-700 px-3 py-2 text-sm"
                    value={newEngineer.capacity}
                    onChange={(e) =>
                      setNewEngineer((s) => ({
                        ...s,
                        capacity: Number(e.target.value) || 1
                      }))
                    }
                    required
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm mb-1">
                  Skills <span className="text-xs text-slate-400">(comma separated)</span>
                </label>
                <input
                  className="w-full rounded-md bg-slate-800 border border-slate-700 px-3 py-2 text-sm"
                  value={newEngineer.skills}
                  onChange={(e) =>
                    setNewEngineer((s) => ({ ...s, skills: e.target.value }))
                  }
                  placeholder="react, java, kafka"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="inline-flex items-center justify-center rounded-md bg-emerald-500 px-4 py-2 text-sm font-medium text-slate-950 hover:bg-emerald-400 disabled:opacity-60"
              >
                {loading ? 'Saving…' : 'Save Engineer'}
              </button>
            </form>

            <div className="rounded-xl bg-slate-900/60 p-4 border border-slate-800 overflow-auto">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-lg font-semibold">Engineer Roster</h2>
                <span className="text-xs text-slate-400">
                  {engineers.length} total
                </span>
              </div>
              <table className="w-full text-sm border-separate border-spacing-y-2">
                <thead className="text-xs text-slate-400">
                  <tr>
                    <th className="text-left px-2">Name</th>
                    <th className="text-left px-2">Role</th>
                    <th className="text-left px-2">Load</th>
                    <th className="text-left px-2">Skills</th>
                  </tr>
                </thead>
                <tbody>
                  {engineers.map((e) => (
                    <tr key={e.id} className="bg-slate-800/60">
                      <td className="px-2 py-1">{e.name}</td>
                      <td className="px-2 py-1 text-xs">
                        {e.designation.replace('_', ' ')}
                      </td>
                      <td className="px-2 py-1 text-xs">
                        {e.currentLoad}/{e.capacity}
                      </td>
                      <td className="px-2 py-1 text-xs text-slate-300">
                        {e.skills.join(', ')}
                      </td>
                    </tr>
                  ))}
                  {engineers.length === 0 && (
                    <tr>
                      <td
                        className="px-2 py-4 text-center text-sm text-slate-500"
                        colSpan={4}
                      >
                        No engineers yet. Add one using the form.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        );
      case 'queries':
        return (
          <div className="grid gap-6 md:grid-cols-[minmax(0,1.3fr)_minmax(0,1.7fr)]">
            <form
              onSubmit={handleCreateQuery}
              className="space-y-4 rounded-xl bg-slate-900/60 p-4 border border-slate-800"
            >
              <h2 className="text-lg font-semibold mb-1">Register Query</h2>
              <p className="text-xs text-slate-400 mb-2">
                Describe the issue in natural language. IntelliRoute uses Gemini to infer complexity and route it to the best engineer.
              </p>
              <div>
                <label className="block text-sm mb-1">Description</label>
                <textarea
                  className="w-full min-h-[96px] rounded-md bg-slate-800 border border-slate-700 px-3 py-2 text-sm resize-y"
                  value={newQuery.description}
                  onChange={(e) =>
                    setNewQuery((s) => ({ ...s, description: e.target.value }))
                  }
                  required
                />
              </div>
              <div className="flex gap-3">
                <div className="flex-1">
                  <label className="block text-sm mb-1">Priority</label>
                  <select
                    className="w-full rounded-md bg-slate-800 border border-slate-700 px-3 py-2 text-sm"
                    value={newQuery.priority}
                    onChange={(e) =>
                      setNewQuery((s) => ({
                        ...s,
                        priority: e.target.value as Priority
                      }))
                    }
                  >
                    {priorityOptions.map((p) => (
                      <option key={p.value} value={p.value}>
                        {p.label}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="flex-1">
                  <label className="block text-sm mb-1">Domain (optional)</label>
                  <input
                    className="w-full rounded-md bg-slate-800 border border-slate-700 px-3 py-2 text-sm"
                    value={newQuery.domain}
                    onChange={(e) =>
                      setNewQuery((s) => ({ ...s, domain: e.target.value }))
                    }
                    placeholder="payments, infra, frontend…"
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm mb-1">
                  Tags <span className="text-xs text-slate-400">(comma separated)</span>
                </label>
                <input
                  className="w-full rounded-md bg-slate-800 border border-slate-700 px-3 py-2 text-sm"
                  value={newQuery.tags}
                  onChange={(e) =>
                    setNewQuery((s) => ({ ...s, tags: e.target.value }))
                  }
                  placeholder="kafka, infra, react"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="inline-flex items-center justify-center rounded-md bg-sky-500 px-4 py-2 text-sm font-medium text-slate-950 hover:bg-sky-400 disabled:opacity-60"
              >
                {loading ? 'Saving…' : 'Save Query'}
              </button>
            </form>

            <div className="rounded-xl bg-slate-900/60 p-4 border border-slate-800 overflow-auto">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-lg font-semibold">Query Backlog</h2>
                <button
                  onClick={handleRunAssignments}
                  disabled={loading}
                  className="inline-flex items-center rounded-md bg-emerald-500 px-3 py-1.5 text-xs font-medium text-slate-950 hover:bg-emerald-400 disabled:opacity-60"
                >
                  {loading ? 'Assigning…' : 'Run Assignment'}
                </button>
              </div>
              <table className="w-full text-sm border-separate border-spacing-y-2">
                <thead className="text-xs text-slate-400">
                  <tr>
                    <th className="text-left px-2">Summary</th>
                    <th className="text-left px-2">Priority</th>
                    <th className="text-left px-2">Complexity</th>
                    <th className="text-left px-2">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {queries.map((q) => (
                    <tr key={q.id} className="bg-slate-800/60 align-top">
                      <td className="px-2 py-2 text-xs text-slate-100 max-w-md">
                        <div className="line-clamp-3">{q.description}</div>
                        {q.tags.length > 0 && (
                          <div className="mt-1 text-[10px] text-slate-400">
                            {q.tags.join(', ')}
                          </div>
                        )}
                      </td>
                      <td className="px-2 py-2 text-xs">
                        <span className="inline-flex rounded-full bg-slate-800 px-2 py-0.5">
                          {q.priority}
                        </span>
                      </td>
                      <td className="px-2 py-2 text-xs">
                        {q.complexityScore != null ? q.complexityScore.toFixed(2) : '–'}
                      </td>
                      <td className="px-2 py-2 text-xs">
                        <span className="inline-flex rounded-full bg-slate-800 px-2 py-0.5">
                          {q.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                  {queries.length === 0 && (
                    <tr>
                      <td
                        className="px-2 py-4 text-center text-sm text-slate-500"
                        colSpan={4}
                      >
                        No queries yet. Create one to see routing.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        );
      case 'assignments':
        return (
          <div className="rounded-xl bg-slate-900/60 p-4 border border-slate-800">
            <div className="flex items-center justify-between mb-3">
              <h2 className="text-lg font-semibold">Active Assignments</h2>
              <button
                onClick={handleRunAssignments}
                disabled={loading}
                className="inline-flex items-center rounded-md bg-emerald-500 px-3 py-1.5 text-xs font-medium text-slate-950 hover:bg-emerald-400 disabled:opacity-60"
              >
                {loading ? 'Refreshing…' : 'Refresh & Assign'}
              </button>
            </div>
            <table className="w-full text-sm border-separate border-spacing-y-2">
              <thead className="text-xs text-slate-400">
                <tr>
                  <th className="text-left px-2">Engineer</th>
                  <th className="text-left px-2">Query</th>
                  <th className="text-left px-2">Status</th>
                  <th className="text-left px-2">Actions</th>
                </tr>
              </thead>
              <tbody>
                {assignments.map((a) => {
                  const q = queryById(a.queryId);
                  const isCompleted = a.status === 'COMPLETED';
                  return (
                    <tr key={a.id} className="bg-slate-800/60 align-top">
                      <td className="px-2 py-2 text-xs">
                        <div className="font-medium">{engineerNameById(a.engineerId)}</div>
                      </td>
                      <td className="px-2 py-2 text-xs max-w-md">
                        <div className="line-clamp-3">
                          {q?.description ?? `Query ${a.queryId}`}
                        </div>
                        {q && (
                          <div className="mt-1 text-[10px] text-slate-400">
                            {q.priority} • {q.status}
                            {q.complexityScore != null &&
                              ` • score ${q.complexityScore.toFixed(2)}`}
                          </div>
                        )}
                      </td>
                      <td className="px-2 py-2 text-xs">
                        <span
                          className={`inline-flex rounded-full px-2 py-0.5 ${
                            isCompleted ? 'bg-emerald-700/70' : 'bg-amber-600/70'
                          }`}
                        >
                          {a.status}
                        </span>
                      </td>
                      <td className="px-2 py-2 text-xs">
                        <button
                          disabled={isCompleted || loading}
                          onClick={() => handleToggleComplete(a)}
                          className={`inline-flex items-center rounded-md px-3 py-1 text-xs font-medium ${
                            isCompleted
                              ? 'bg-slate-700 text-slate-300 cursor-default'
                              : 'bg-emerald-500 text-slate-950 hover:bg-emerald-400'
                          } disabled:opacity-60`}
                        >
                          {isCompleted ? 'Done' : 'Mark Complete'}
                        </button>
                      </td>
                    </tr>
                  );
                })}
                {assignments.length === 0 && (
                  <tr>
                    <td
                      className="px-2 py-4 text-center text-sm text-slate-500"
                      colSpan={4}
                    >
                      No assignments yet. Run assignment from the Queries tab.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        );
    }
  };

  return (
    <div className="min-h-screen">
      <header className="border-b border-slate-800 bg-slate-950/80 backdrop-blur">
        <div className="mx-auto max-w-6xl px-4 py-4 flex items-center justify-between">
          <div>
            <div className="text-sm uppercase tracking-[0.25em] text-emerald-400">
              IntelliRoute
            </div>
            <div className="text-lg font-semibold text-slate-50">
              Engineering Triage Console
            </div>
          </div>
          <div className="text-xs text-slate-400">
            Backend: <span className="text-emerald-400">Spring Boot + Gemini</span>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px  -4 py-6 space-y-4 px-4">
        <nav className="inline-flex rounded-full bg-slate-900/80 border border-slate-800 p-1 text-xs mb-2">
          {(
            [
              ['engineers', 'Engineers'],
              ['queries', 'Queries'],
              ['assignments', 'Assignments']
            ] as [Tab, string][]
          ).map(([key, label]) => (
            <button
              key={key}
              onClick={() => setTab(key)}
              className={`px-4 py-1.5 rounded-full ${
                tab === key
                  ? 'bg-slate-100 text-slate-900 font-medium'
                  : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              {label}
            </button>
          ))}
        </nav>

        {error && (
          <div className="rounded-md border border-red-500/60 bg-red-500/10 px-3 py-2 text-xs text-red-100">
            {error}
          </div>
        )}

        {renderTab()}
      </main>
    </div>
  );
}

export default App;


