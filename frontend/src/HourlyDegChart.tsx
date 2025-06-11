import axios from "axios";
import { useState, useEffect } from "react";
import { AreaChart, Area } from "recharts";

const HourlyDegChart = () => {

    const[chartData, setChartData] = useState([]);
    const[hoursOfDay, setHoursOfDay] = useState<any[]>([])
    
    const getWeatherHourlyData = () => {
        axios({
            method: 'get',
            url: 'http://localhost:5000/api/current-hourly-chart-data',
        }).then(response => setChartData(response.data));
    }

    const getHoursData = () => {
        axios({
            method: 'get',
            url: 'http://localhost:5000/api/current-hours-data',
        }).then(response => setHoursOfDay(response.data));
    }

    useEffect(() => {
        getWeatherHourlyData();
        getHoursData();
    })

    return (
    <div className='hourly-deg-chart'>
        <div className='chart'>
            <AreaChart width={1000} height={200} data={chartData}>
                <Area type="monotone" dataKey="" stroke="FFE600" fill="#FCEF91"/>
            </AreaChart>
        </div>
        <div className='hours-of-day'>

            {hoursOfDay.map((item) => (

                <p className='circular-hours'>{item.time}</p>

            ))}
        </div>
    </div>
    )

}

export default HourlyDegChart;