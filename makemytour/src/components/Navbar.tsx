"use client";

import React, { useEffect, useState } from "react";
import SignupDialog from "./SignupDialog";
import { Bell, LogOut, Plane, User, Menu, X } from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "./ui/button";
import { Avatar, AvatarFallback } from "./ui/avatar";
import { clearUser, setUser } from "@/store";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Notification from "./Notification";
import { getuserbytoken } from "@/api";

const Navbar = () => {
  const dispatch = useDispatch();
  const user = useSelector((state: any) => state.user.user);
  const router = useRouter();
  const [openMenu, setOpenMenu] = useState(false);

  const logout = () => {
    dispatch(clearUser());
    setOpenMenu(false);
  };

  useEffect(() => {
    const token = localStorage.getItem("Authorization");

    const fetchuser = async () => {
      try {
        const user1 = await getuserbytoken();
        dispatch(setUser(user1));
      } catch (e) {
        console.error(e);
      }
    };

    if (token) fetchuser();
  }, [dispatch]);

  return (
    <header className="backdrop-blur-md py-4 sticky top-0 z-50 bg-white">
      <div className="container mx-auto px-4 flex items-center justify-between">

        <div className="flex items-center space-x-2">
          <Plane className="w-8 h-8 text-red-500" />
          <span className="text-2xl font-bold text-black">MakeMyTour</span>
        </div>

        <div className="flex items-center gap-4">

          {user ? (
            <>
              <Notification trigger={<Bell className="w-6 h-6 text-gray-600" />} />
              <div className="hidden md:flex items-center gap-4">
                <Link href="/dashboard">Dashboard</Link>

                {user.role === "ADMIN" && (
                  <Button onClick={() => router.push("/admin")}>
                    ADMIN
                  </Button>
                )}
              </div>

              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button
                    variant="ghost"
                    className="relative h-8 w-8 rounded-full"
                  >
                    <Avatar className="h-8 w-8">
                      <AvatarFallback>
                        {user?.firstName?.charAt(0)}
                      </AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>

                <DropdownMenuContent className="w-56" align="end" forceMount>
                  <DropdownMenuLabel className="font-normal">
                    <div className="flex flex-col space-y-1">
                      <p className="text-sm font-medium leading-none">
                        {user?.firstName}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {user?.email}
                      </p>
                    </div>
                  </DropdownMenuLabel>

                  <DropdownMenuSeparator />

                  <DropdownMenuItem onClick={() => router.push("/profile")}>
                    <User className="mr-2 h-4 w-4" />
                    Profile
                  </DropdownMenuItem>

                  <DropdownMenuItem onClick={logout}>
                    <LogOut className="mr-2 h-4 w-4" />
                    Log out
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
              <button
                className="md:hidden"
                onClick={() => setOpenMenu(true)}
              >
                <Menu />
              </button>
            </>
          ) : (
            <SignupDialog
              trigger={
                <Button className="bg-blue-600 text-white">
                  Sign Up
                </Button>
              }
            />
          )}
        </div>
      </div>

      <div
        className={`fixed top-0 right-0 h-full w-64 bg-white shadow-lg transform transition-transform duration-300 z-50
        ${openMenu ? "translate-x-0" : "translate-x-full"}`}
      >
        <div className="flex justify-between items-center p-4 border-b">
          <span className="font-bold">Menu</span>
          <X onClick={() => setOpenMenu(false)} className="cursor-pointer" />
        </div>

        <div className="flex flex-col p-4 gap-4">
          <Link href="/dashboard" onClick={() => setOpenMenu(false)}>
            Dashboard
          </Link>

          {user?.role === "ADMIN" && (
            <Button
              onClick={() => {
                router.push("/admin");
                setOpenMenu(false);
              }}
            >
              ADMIN
            </Button>
          )}
        </div>
      </div>

      {openMenu && (
        <div
          className="fixed inset-0 bg-black/40 z-40"
          onClick={() => setOpenMenu(false)}
        />
      )}
    </header>
  );
};

export default Navbar;