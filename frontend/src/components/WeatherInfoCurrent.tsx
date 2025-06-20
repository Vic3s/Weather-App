import axios from "axios";
import { useState, useEffect } from "react";
import type { WeatherDataType } from "../aditional/Types";

const WeatherInfoCurrent = () => {


  const[currWeatherData, setCurrWeatherData] = useState(Array<any>)
  const[weatherMetric, setWeatherMetric] = useState<WeatherDataType>(Object)

  const getCurrentTimeWeather = () => {
    axios({
      method: 'get',
      url: 'http://localhost:5000/api/curr-time-weather',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(response => {
      setCurrWeatherData(response.data)
      setWeatherMetric(response.data[0])
    })
  }

  useEffect(() => {
    getCurrentTimeWeather();
  }, [])

    return (
        <div className='weather-info-current'>
          <div className='current-deg'>
            <h1 className='deg'>{weatherMetric.degrees}</h1>
            <div className='celcius-fahrenheit'>
              <p className='celcius' onClick={(e) => setWeatherMetric(currWeatherData[0])}>C°</p>
              |
              <p className='fahrenheit' onClick={(e) => setWeatherMetric(currWeatherData[1])}>F°</p>
            </div>
            <div className='weather-sub-data'>
              <p className='rain'><b>Rain:</b> {weatherMetric.rain} mm</p>
              <p className='humidity'><b>Humidity:</b> {weatherMetric.humidity}%</p>
              <p className='wind'><b>Wind:</b> {weatherMetric.wind} km/h</p>
            </div>
          </div>
          <div className='current-time-condition'>
            <h3 className='day-time'>{`${weatherMetric.dow}, ${weatherMetric.time}`}</h3>
            <h2>{weatherMetric.condition}</h2>
          </div>
        </div>
    )
}

export default WeatherInfoCurrent;