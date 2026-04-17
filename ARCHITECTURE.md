## 🏗️ System Architecture Overview

### 📐 Component Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Flight & Hotel Booking App                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────┐         ┌──────────────────────┐    │
│  │   Seat Selection     │         │  Room Selection      │    │
│  │   (Seat/index.tsx)   │         │  (roomDetail/index)  │    │
│  └──────────────────────┘         └──────────────────────┘    │
│           │                                │                   │
│           ├─→ Seat Component          ├─→ Room3DPreview       │
│           ├─→ Premium Logic           ├─→ Room Type Grid      │
│           └─→ Pricing Calc            └─→ Multi-select        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│              Data Flow & State Management                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐        ┌───────────────┐   ┌──────────────┐  │
│  │   Redux     │←----→  │ localStorage  │   │  sessionState│  │
│  │   Store     │        │ (Preferences) │   │              │  │
│  └─────────────┘        └───────────────┘   └──────────────┘  │
│       │                                                         │
│       ├─ flights.seatPreferences[]                            │
│       ├─ hotels.roomPreferences[]                             │
│       ├─ flights.flight[]                                     │
│       └─ hotels.selectedRoom                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│         Real-time Updates & Notifications                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐      ┌──────────────┐    ┌──────────────┐   │
│  │ useAvailability │  │Notification  │    │ Track Changes│   │
│  │   Hooks      │    │ Component    │    │   Handler    │   │
│  └──────────────┘    └──────────────┘    └──────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│        Backend API & Real-time Services                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  REST API    WebSocket    Database                           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### 🔄 Data Flow Diagram

```
User Interaction
       │
       ├─ SELECT SEATS/ROOMS
       │  ├─→ validate available ✓
       │  ├─→ update component state
       │  ├─→ calculate pricing
       │  └─→ update Redux store
       │
       ├─ REAL-TIME CHECK
       │  ├─→ poll backend (3s interval)
       │  ├─→ check availability changes
       │  └─→ notify user if limited
       │
       ├─ SAVE PREFERENCE
       │  ├─→ validate preference data
       │  ├─→ save to localStorage
       │  ├─→ dispatch Redux action
       │  └─→ show confirmation
       │
       └─ CONFIRM BOOKING
          ├─→ validate selections
          ├─→ calculate final price
          ├─→ send booking request
          └─→ redirect to payment
```

---

### 📊 State Management Structure

```
Redux Store
│
├─ flights
│  ├─ flight (array)
│  ├─ seatType (string)
│  └─ seatPreferences (array)
│
├─ hotels
│  ├─ hotels (object)
│  ├─ selectedRoom (object)
│  └─ roomPreferences (array)
│
└─ user
   └─ user (object)

localStorage (makemytrip_preferences)
│
├─ seatPreferences (array, max 10)
└─ roomPreferences (array, max 10)
```

---

### 🎨 UI Component Hierarchy

```
App
│
├─ Seat Selection Page
│  ├─ Header Info
│  ├─ Seat Matrix
│  │  └─ Seat Component (×200+)
│  ├─ Selection Summary
│  ├─ Booking Dialog
│  └─ Save Preference Card
│
└─ Room Detail Page
   ├─ Header (Hotel Info)
   ├─ Left Column
   │  ├─ Room3DPreview
   │  └─ Room Type Selection
   └─ Right Column
      ├─ Booking Summary
      ├─ Price Breakdown
      └─ Booking Button
```

---

**Architecture Version:** 1.0  
**Last Updated:** March 28, 2026  
**Status:** Ready for Implementation
