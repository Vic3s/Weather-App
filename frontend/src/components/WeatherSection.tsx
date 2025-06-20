import HourlyDegChart from "./HourlyDegChart"
import WeatherInfoCurrent from "./WeatherInfoCurrent"
import WeeklyDegCurrent from "./WeeklyDegCurrent"

export const WeatherSection = () => {


    return (
        <>
            <div className='weather-section'>

                <WeatherInfoCurrent />
                
                <HourlyDegChart />

                <WeeklyDegCurrent />
            
            </div>
            
        </>
    )
}