import axios from "axios";
import { useState, useEffect } from "react";
import type { WeatherDataType } from "../aditional/Types";

const WeatherInfoCurrent = () => {

  const[currWeatherData, setCurrWeatherData] = useState<WeatherDataType>(Object)

  const getCurrentTimeWeather = () => {
    axios({
      method: "get",
      url: 'http://localhost:5000/api/curr-time-weather',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(response => {
      setCurrWeatherData(response.data)
    }).catch(err => console.log(err));
  }

  useEffect(() => {
    getCurrentTimeWeather();
  }, [])

    return (
        <div className='weather-info-current'>
          <div className='current-deg'>
            <h1 className='deg'>{currWeatherData.degrees}°</h1>
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