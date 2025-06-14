import axios from "axios"
import { useState, useEffect} from "react"

const WeeklyDegCurrent = () => {

    const[nextWeekData, setNextWeekData] = useState<any[]>([]);

    const getNextWeekDegs = () => {
        axios({
            method: "get",
            url: 'http://localhost:5000/api/next-seven-days-data'
        }).then(response => setNextWeekData(response.data))
    }

    useEffect(() => {
        getNextWeekDegs();
    })

    return (
        <div className='weekly-deg-current'>
            {nextWeekData.map((item) => (
                <div className='card-day'>
                <h4 className='day-of-week'>{item.day}</h4>
                <i className="fa-solid fa-sun fa-2xl weather-icon"></i>
                <div className='deg-range'>
                    <p className='deg-hi'>{item.deg_hi}</p>
                    <p className='deg-lo'>{item.deg_lo}</p>
                </div>
                </div>
            ))}
            
        </div>
    )

}

export default WeeklyDegCurrent;