import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { connectSocket, disconnectSocket } from "./Socket";
import { updateHotel, updateFlight, setSeatmatrix, postNotification } from "@/store";

export default function SocketProvider() {
  const dispatch = useDispatch();
  const flight = useSelector((state: any) => state.flights.flight)
  const seatType = useSelector((state: any) => state.flights.seatType)

  useEffect(() => {
    connectSocket({
      onHotel: (data) => dispatch(updateHotel(data)),
      onFlight: (data) => {
        dispatch(updateFlight(data));

        if (!seatType) {
          dispatch(postNotification({
            entityId: data.id,
            messages: {
              message: `Flight ${data.id} | ${data.from}→${data.to} status changed to ${data.status}`,
              updatedDate: new Date(data.departureTime).toISOString(),
              dateTime: new Date().toISOString()
            }
          }));
        }
        if (data.seatType && seatType) {
          if (
            data.seatType.charAt(0).toLowerCase() ===
            seatType.charAt(0).toLowerCase()
          ) {
            if (data.seatsMatrix) { dispatch(setSeatmatrix(data.seatsMatrix)); }
          }
        }
      },
    });

    return () => disconnectSocket();
  }, []);

  return null;
}