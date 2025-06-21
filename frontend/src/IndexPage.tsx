import axios from 'axios'
import { useState } from 'react'
import './styles/index-page.css'
import { WeatherSection } from './components/WeatherSection'

const IndexPage = () => {

  const[cityName, setCityName] = useState("")

  const postWeatherForcastCity = (e: any) => {
    e.preventDefault();
    const data = {
      'cityName': cityName,
    }
    axios.post('http://localhost:5000/api/post-city-name', data,
      {
        headers: {
          'Content-Type': 'application/json'
        }
      })
    .then(response => {
      setCityName(response.data)
      window.location.reload()
    })
    .catch(err => console.log(err));
  }

  return (
    <>
    <div className='main-content'>
      <div className='heading-main'>
        <h1>Weather Forcast</h1>
      </div>

      <form onSubmit={(e) => {postWeatherForcastCity(e);}}>
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-md-6">
              <div className="search-container">
                <input type="text" 
                className="form-control search-input" 
                placeholder="Search..."
                onChange={(e) => setCityName(e.target.value)}
                />
                <i className="fas fa-search search-icon"></i>
              </div>
            </div>
          </div>
        </div>
      </form>

        <WeatherSection />

    </div>
    </>
  )
}

export default IndexPage;
