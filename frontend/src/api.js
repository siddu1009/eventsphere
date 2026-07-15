// Keep browser requests on the frontend origin. Vite proxies /api in development;
// a production reverse proxy forwards the same path to the backend.
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

export async function fetchEvents() {
  const response = await fetch(`${API_BASE_URL}/events`);
  if (!response.ok) {
    throw new Error('Failed to load events');
  }
  return response.json();
}

export async function loginUser(email, password) {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
  if (!response.ok) throw new Error((await response.json().catch(() => ({}))).message || 'Login failed');
  return response.json();
}

export async function registerUser(payload) {
  const response = await fetch(`${API_BASE_URL}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  if (!response.ok) throw new Error((await response.json().catch(() => ({}))).message || 'Registration failed');
  return response.json();
}

export async function fetchEvent(id) {
  const response = await fetch(`${API_BASE_URL}/events/${id}`);
  if (!response.ok) throw new Error('Failed to load event');
  return response.json();
}

export async function authorizedRequest(path, token, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}`, ...options.headers },
  });
  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    throw new Error(body.message || 'Request failed');
  }
  return response.status === 204 ? null : response.json();
}

export function createBooking(token, payload) {
  return authorizedRequest('/bookings', token, { method: 'POST', body: JSON.stringify(payload) });
}

export function fetchBookings(token) { return authorizedRequest('/bookings', token); }
export function fetchUserBookings(token) { return authorizedRequest('/users/bookings', token); }
export function fetchBooking(token, bookingId) { return authorizedRequest(`/bookings/${bookingId}`, token); }
export function createPayment(token, bookingId, method) { return authorizedRequest('/payments', token, { method: 'POST', body: JSON.stringify({ bookingId, method }) }); }
export function fetchTicket(token, bookingId) { return authorizedRequest(`/tickets/${bookingId}`, token); }

export function cancelBooking(token, bookingId) { return authorizedRequest(`/bookings/${bookingId}/cancel`, token, { method: 'POST' }); }

export function createEvent(token, event) { return authorizedRequest('/events', token, { method: 'POST', body: JSON.stringify(event) }); }
export function updateEvent(token, id, event) { return authorizedRequest(`/events/${id}`, token, { method: 'PUT', body: JSON.stringify(event) }); }
export function deleteEvent(token, id) { return authorizedRequest(`/events/${id}`, token, { method: 'DELETE' }); }
export function fetchOrganizerEvents(token) { return authorizedRequest('/organizer/events', token); }
export function fetchAttendees(token, id) { return authorizedRequest(`/organizer/events/${id}/attendees`, token); }
export function fetchOrganizerAnalytics(token) { return authorizedRequest('/organizer/analytics', token); }
export function fetchAdminUsers(token) { return authorizedRequest('/admin/users', token); }
export function updateAdminUser(token, id, update) { return authorizedRequest(`/admin/users/${id}`, token, { method: 'PUT', body: JSON.stringify(update) }); }
export function fetchAdminStatistics(token) { return authorizedRequest('/admin/statistics', token); }
export function fetchAdminReports(token) { return authorizedRequest('/admin/reports', token); }
export function fetchCategories() { return fetch(`${API_BASE_URL}/categories`).then((response) => { if (!response.ok) throw new Error('Failed to load categories'); return response.json(); }); }
export function createCategory(token, name) { return authorizedRequest('/categories', token, { method: 'POST', body: JSON.stringify({ name }) }); }
export function deleteCategory(token, id) { return authorizedRequest(`/categories/${id}`, token, { method: 'DELETE' }); }
