import axios from "axios";
import { useState, useEffect } from "react";

const WeatherInfoCurrent = () => {

  type WeatherDataType = {
    degrees: number,
    rain: string,
    humidity: string,
    wind: string,
    dow: string,
    time: number,
    condition: string
  }

  const[currWeatherData, setCurrWeatherData] = useState<WeatherDataType>(Object)

  const getCurrentTimeWeather = () => {
    axios({
      method: 'get',
      url: 'http://localhost:5000/api/curr-time-weather',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(response => setCurrWeatherData(response.data))
  }

  useEffect(() => {
    getCurrentTimeWeather();
  }, [])

    return (
        <div className='weather-info-current'>
          <div className='current-deg'>
            <h1 className='deg'>{currWeatherData.degrees}</h1>
            <div className='celcius-fahrenheit'>
              <p className='celcius'>C°</p>
              |
              <p className='fahrenheit'>F°</p>
            </div>
            <div className='weather-sub-data'>
              <p className='rain'><b>Rain:</b> {currWeatherData.rain} mm</p>
              <p className='humidity'><b>Humidity:</b> {currWeatherData.humidity}%</p>
              <p className='wind'><b>Wind:</b> {currWeatherData.wind} km/h</p>
            </div>
          </div>
          <div className='current-time-condition'>
            <h3 className='day-time'>{`${currWeatherData.dow}, ${currWeatherData.time}`}</h3>
            <h2>{currWeatherData.condition}</h2>
          </div>
        </div>
    )
}

export default WeatherInfoCurrent;