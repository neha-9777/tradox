import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Navbar from './Navbar';

export default function AppLayout() {
  return (
    <div className="min-h-screen bg-slate-950">
      <Sidebar />
      {/* Main content — offset by sidebar width (w-64 = 256px, collapsed = 72px) */}
      <main className="ml-64 min-h-screen transition-all duration-300 flex flex-col">
        <Navbar />
        <div className="p-6 lg:p-8 flex-1">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

