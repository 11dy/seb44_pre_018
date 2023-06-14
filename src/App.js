import Router from './components/Router';
import Header from './components/global/Header';
import Navbar from './components/global/Navbar';
import './index.css';
import './style/global.scss';
import './style/reset.scss';

function App() {
  return (
    <div className="App">
      <Header />
      <section className="flex">
        <Navbar />
        <div className="mainWrap">
          <Router />
        </div>
      </section>
    </div>
  );
}

export default App;
