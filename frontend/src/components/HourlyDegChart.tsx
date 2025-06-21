import axios from "axios";
import { useState, useEffect } from "react";
import { AreaChart, Area, Tooltip, XAxis, LabelList} from "recharts";

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
            method: "get",
            url: 'http://localhost:5000/api/current-hours-data',
        }).then(response => setHoursOfDay(response.data))
        .catch(err => console.log(err));
    }

    useEffect(() => {
        getWeatherHourlyData();
        getHoursData();
    }, [])

    return (
    <div className='hourly-deg-chart'>
        <div className='chart'>
                <AreaChart data={chartData} width={900} height={150}>
                    <XAxis dataKey="degrees" interval="preserveStartEnd"/>
                    <Tooltip/>
                    <Area 
                    type="monotone"
                    dataKey="degrees" 
                    stroke="FFE600" 
                    fill="#FCEF91" 
                    dot/>
                    <LabelList dataKey="value" position="top"/>
                </AreaChart>
        </div>
        <div className='hours-of-day'>

            {hoursOfDay.map((item) => (

                <p className='circular-hours'>{item}:00</p>

            ))}
        </div>
    </div>
    )

}

export default HourlyDegChart;