import axios from "axios"
import { useState, useEffect} from "react"
import type { WeatherDayShort } from "../aditional/Types";

const WeeklyDegCurrent = () => {

    const[nextWeekData, setNextWeekData] = useState<any[]>([]);

    const getNextWeekDegs = () => {
        axios({
            method: "get",
            url: 'http://localhost:5000/api/next-seven-days-data'
        }).then(response => setNextWeekData(response.data))
        .catch(err => console.log(err))
    }

    useEffect(() => {
        getNextWeekDegs();
    }, [])

    return (
        <div className='weekly-deg-current'>
            {nextWeekData.map((item: WeatherDayShort) => (
                <div className='card-day'>
                <h4 className='day-of-week'>{item.day}</h4>
                <img src={item.weatherIcon} alt="Weather Condition Icon" />
                <div className='deg-range'>
                    <p className='deg-hi'>{item.maxTemp}°</p>
                    <p className='deg-lo'>{item.minTemp}°</p>
                </div>
                </div>
            ))}
        </div>
    )

}

export default WeeklyDegCurrent;