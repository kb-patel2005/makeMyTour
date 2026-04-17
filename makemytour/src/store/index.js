import { configureStore, createSlice } from "@reduxjs/toolkit";

const BACKEND_URL = "http://localhost:8080";

import { createAsyncThunk } from "@reduxjs/toolkit";

export const postNotification = createAsyncThunk(
  "notifications/postNotification",
  async (notification, { dispatch, rejectWithValue }) => {
    try {
      const token = localStorage.getItem("Authorization");
      const response = await fetch(`${BACKEND_URL}/notify`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(notification),
      });
      dispatch(liveNotification(notification.messages));
      if (!response.ok) {
        const error = await response.text();
        return rejectWithValue(error);
      }

      return await response.json();
    } catch (err) {
      return rejectWithValue(err.message);
    }
  }
);

export const addNotificationAsync = createAsyncThunk(
  "notifications/addNotificationAsync",
  async (ids) => {
    const results = await Promise.all(
      ids.map(id => getNotificationById(id))
    );
    return results;
  }
);

const getNotificationById = async (id) => {
  const token = localStorage.getItem("Authorization")
  try {
    const data = await fetch(`${BACKEND_URL}/notifications/${id}`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });
    const res = data.json();
    return res;
  } catch (error) {
    alert(error)
  }
}

export const fetchNotifications = createAsyncThunk(
  "notifications/fetchNotifications",
  async (ids) => {
    const results = await Promise.all(
      ids.map(id => getNotificationById(id))
    );

    return results;
  }
);

const hotelslice = createSlice({
  name: "hotels",
  initialState: { hotels: null, selectedRoom: null },
  reducers: {
    setHotels: (state, action) => { state.hotels = action.payload; },
    setRoom: (state, action) => { state.selectedRoom = action.payload; },
    updateHotel: (state, action) => {
      const newHotel = action.payload;
      if (!newHotel) return;

      console.log("Updating hotel:", newHotel);

      // ✅ always create NEW reference (important)
      if (Array.isArray(state.hotels)) {
        state.hotels = state.hotels.map((h) =>
          h._id === newHotel._id ? newHotel : h
        );
      } else if (state.hotels?._id === newHotel._id) {
        state.hotels = { ...newHotel };
      }

      if (state.selectedRoom && newHotel.rooms) {
        const updatedRoom = newHotel.rooms.find(
          (r) => r.type === state.selectedRoom.type
        );

        console.log("🧠 OLD:", state.selectedRoom?.availability);
        console.log("🧠 NEW:", updatedRoom?.availability);

        if (updatedRoom) {
          state.selectedRoom = {
            ...updatedRoom,
            availability: [...updatedRoom.availability] // 🔥 force new reference
          };
        }
      }
    }
  },
});

const flightSlice = createSlice({
  name: "flights",
  initialState: { flight: null, quantity: 0, seatType: "", seatMatrix: null },
  reducers: {
    setFlight: (state, action) => { state.flight = action.payload },
    setSeatType: (state, action) => { state.seatType = action.payload },
    setQty: (state, action) => { state.quantity = action.payload },
    setSeatmatrix: (state, action) => { state.seatMatrix = action.payload.map((row) => [...row]); },
    updateFlight: (state, action) => {
      const newFlight = action.payload;
      if (!state.flight || state.flight.length == 0) { }
      else if (newFlight.seatType == null) {
        state.flight.map((data, index) => {
          if (data.id == newFlight.id) {
            state.flight[index] = { ...state.flight[index], status: newFlight.status }
          }
        })
      } else {
        const index = state.flight.findIndex((f) => f.id == newFlight.id);
        if (index !== -1) {
          state.seatType.toLowerCase.charAt(0) == "e"?
          state.flight[index] = {...state.flight[index], economicseats:newFlight.seatsMatrix }:
          state.flight[index] = {...state.flight[index], bussinesseats:newFlight.seatsMatrix };
        }
      }
    }
  }
});

const notificationSlice = createSlice({
  name: "notifications",
  initialState: { notifications: [] },
  reducers: {
    addNotification: (state, action) => {
      action.payload.map(async (e) => {
        const res = await getNotificationById(e);
        alert(JSON.stringify(res))
        alert(JSON.stringify(res.messages))
        if (Object.keys(res).length > 0) {
          state.notifications.unshift(...res.messages)
        }
      })
    },
    liveNotification: (state, action) => {
      state.unshift(action.payload);
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(postNotification.fulfilled, (state, action) => {
        state.loading = false;
        state.unshift(action.payload);
      })
      .addCase(fetchNotifications.fulfilled, (state, action) => {
        action.payload.forEach((res) => {
          if (res && Object.keys(res).length > 0) {
            if (Array.isArray(res.messages)) {
              state.notifications.unshift(...res.messages);
            } else {
              state.notifications.unshift(res.messages);
            }
          }
        });
      })
      .addCase(addNotificationAsync.fulfilled, (state, action) => {
        action.payload.forEach((res) => {
          if (res && Object.keys(res).length > 0) {
            if (Array.isArray(res.messages)) {
              state.notifications.unshift(...res.messages);
            } else {
              state.notifications.unshift(res.messages);
            }
          }
        });
      });
  },
});

const initialState = {
  user: null,
};

const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    setUser: (state, action) => {
      state.user = action.payload;
      console.log(action.payload)
    },
    clearUser: (state) => {
      state.user = null;
      if (typeof window !== "undefined" && localStorage) {
        localStorage.removeItem("Authorization");
      }
    },
  },
});

const store = configureStore({
  reducer: {
    hotels: hotelslice.reducer,
    user: userSlice.reducer,
    flights: flightSlice.reducer,
    notification: notificationSlice.reducer
  },
});


export const { setUser, clearUser } = userSlice.actions;
export const { setHotels, setRoom, updateHotel } = hotelslice.actions;
export const { setFlight, setQty, updateFlight, setSeatType, setSeatmatrix } = flightSlice.actions;
export const { addNotification } = notificationSlice.actions;
export default store;
