export type WeatherDayShort = {
    day:string,
    maxTemp: number,
    minTemp: number,
    weatherIcon: string
}

export type WeatherDataType = {
    degrees: number,
    rain: string,
    humidity: string,
    wind: string,
    dow: string,
    time: number,
    condition: string
  }