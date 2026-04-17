/**
 * Real-time Availability Notification Component
 * Displays live updates about availability changes
 */

import React, { useEffect, useState } from "react";
import { AlertCircle, CheckCircle, Clock, X } from "lucide-react";
import { cn } from "@/lib/utils";
import { useAvailabilityTracker } from "@/hooks/useAvailability";

interface NotificationConfig {
  duration?: number; // Auto-hide after this duration (ms)
  autoHide?: boolean;
  position?: "top" | "bottom";
}

export function AvailabilityNotification({
  duration = 4000,
  autoHide = true,
  position = "top",
}: NotificationConfig) {
  const { changes } = useAvailabilityTracker();
  const [visible, setVisible] = useState(false);
  const [notifications, setNotifications] = useState<string[]>([]);

  useEffect(() => {
    if (changes.size > 0) {
      const latestChange = Array.from(changes.values()).pop();
      if (latestChange) {
        const msg = `${latestChange.itemId} is now ${latestChange.available ? "available" : "unavailable"}`;
        setNotifications((prev) => [msg, ...prev].slice(0, 3)); // Keep last 3
        setVisible(true);

        if (autoHide) {
          const timer = setTimeout(() => {
            setVisible(false);
            setTimeout(() => setNotifications([]), 500);
          }, duration);

          return () => clearTimeout(timer);
        }
      }
    }
  }, [changes, duration, autoHide]);

  if (!visible || notifications.length === 0) return null;

  return (
    <div
      className={cn(
        "fixed left-0 right-0 z-50 p-4 transition-all duration-300",
        position === "top" ? "top-0" : "bottom-0"
      )}
    >
      <div className="max-w-md mx-auto space-y-2">
        {notifications.map((msg, idx) => (
          <div
            key={idx}
            className="bg-white rounded-lg shadow-lg p-4 flex items-start gap-3 border-l-4 border-blue-500 animate-in fade-in slide-in-from-top-2"
          >
            <Clock className="w-5 h-5 text-blue-500 flex-shrink-0 mt-0.5" />
            <div className="flex-1">
              <p className="text-sm font-medium text-gray-900">{msg}</p>
              <p className="text-xs text-gray-500 mt-1">Just now</p>
            </div>
            <button
              onClick={() =>
                setNotifications((prev) => prev.filter((_, i) => i !== idx))
              }
              className="text-gray-400 hover:text-gray-600"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

/**
 * Legacy Seating Availability Alert
 */
export function LimitedAvailabilityWarning({
  availableCount,
  totalCount,
  threshold = 5,
}: {
  availableCount: number;
  totalCount: number;
  threshold?: number;
}) {
  const percentAvailable = Math.round((availableCount / totalCount) * 100);
  const isLimited = availableCount <= threshold;

  if (!isLimited) return null;

  return (
    <div className="bg-orange-50 border border-orange-200 rounded-lg p-4 flex items-start gap-3 mb-4">
      <AlertCircle className="w-5 h-5 text-orange-600 flex-shrink-0 mt-0.5" />
      <div>
        <p className="font-semibold text-orange-900">Limited Availability!</p>
        <p className="text-sm text-orange-800 mt-1">
          Only {availableCount} out of {totalCount} seats/rooms remaining
          ({percentAvailable}%). Book now to secure your selection!
        </p>
      </div>
    </div>
  );
}

/**
 * Booking Confirmation with Availability Status
 */
export function AvailabilityStatus({
  type,
  count,
  isAvailable,
}: {
  type: "seat" | "room";
  count: number;
  isAvailable: boolean;
}) {
  return (
    <div
      className={cn(
        "rounded-lg p-3 flex items-center gap-2",
        isAvailable
          ? "bg-green-50 border border-green-200"
          : "bg-red-50 border border-red-200"
      )}
    >
      {isAvailable ? (
        <CheckCircle className="w-5 h-5 text-green-600" />
      ) : (
        <AlertCircle className="w-5 h-5 text-red-600" />
      )}
      <span
        className={cn(
          "text-sm font-medium",
          isAvailable ? "text-green-900" : "text-red-900"
        )}
      >
        {count} {type}{count !== 1 ? "s" : ""} {isAvailable ? "available" : "unavailable"}
      </span>
    </div>
  );
}
