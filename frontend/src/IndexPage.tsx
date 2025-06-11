import axios from 'axios'
import { useState } from 'react'
import './styles/index-page.css'
import WeatherInfoCurrent  from './WeatherInfoCurrent' 
import HourlyDegChart from './HourlyDegChart'
import WeeklyDegCurrent from './WeeklyDegCurrent'

const IndexPage = () => {

  const[cityName, setCityName] = useState('')
  const[cityCourdinates, setCityCourdinates] = useState({})


  const postWeatherForcastCity = () => {
    const data = {
      'cityName': cityName,
    }
    axios.post('http://localhost:5000/api/post-city-name', data)
    .then(response => setCityCourdinates(response.data))
  }


  return (
    <>
    <div className='main-content'>
      <div className='heading-main'>
        <h1>Weather Forcast</h1>
      </div>

      <form>
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

      <div className='weather-section'>

        <WeatherInfoCurrent />
        
        {/* <HourlyDegChart />

        <WeeklyDegCurrent /> */}
        
      </div>
    
    </div>
    </>
  )
}

export default IndexPage;
