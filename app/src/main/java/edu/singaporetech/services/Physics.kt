package edu.singaporetech.services

import android.util.Log
import java.lang.Float.max
import java.lang.Float.min


class AABB(var min: Vector2, var max: Vector2) {

}


class Physics private constructor() {

    companion object {

        @JvmStatic
        fun simpleAABB(aabb1: AABB, aabb2: AABB) : Boolean{
            return (aabb1.max.x >= aabb2.min.x && aabb1.max.x <= aabb2.max.x
                    && aabb1.max.y >= aabb2.min.y && aabb1.max.y <= aabb2.max.y)
        }
        @JvmStatic
        fun collisionIntersectionRectRect(aabb1: AABB, vel1: Vector2, aabb2: AABB, vel2: Vector2, dt : Float): Boolean {

            // Check for static collision detection between rectangles (before moving).
            if (aabb1.max.x < aabb2.min.x || aabb1.min.x > aabb2.max.x || aabb1.max.y < aabb2.min.y || aabb1.min.y > aabb2.max.y) {
                return false
            }

            // Initialize and calculate the new velocity
            var tFirst = 0f
            var tLast = dt
            val vRel = Vector2(vel2.x - vel1.x, vel2.y - vel1.y)
            // check overlaps for x-axis
            if (vRel.x < 0) {
                // case 1
                if (aabb1.min.x > aabb2.max.x) {
                    return false
                }
                // case 4
                if (aabb1.max.x < aabb2.min.x) {
                    tFirst = (aabb1.max.x - aabb2.min.x) / vRel.x
                }
                if (aabb1.min.x > aabb2.max.x) {
                    tLast = (aabb1.min.x - aabb2.max.x) / vRel.x
                }
            }
            if (vRel.x > 0) {
                // case 2
                if (aabb1.min.x > aabb2.max.x) {
                    tFirst = (aabb1.min.x - aabb2.max.x) / vRel.x
                }
                if (aabb1.max.x > aabb2.min.x) {
                    tLast = (aabb1.max.x - aabb2.min.x) / vRel.x
                }
                // case 3
                if (aabb1.max.x < aabb2.min.x) {
                    return false
                }
            }
            // Repeat checks for y-axis
            if (vRel.y < 0) {
                // case 1
                if (aabb1.min.y > aabb2.max.y) {
                    return false
                }
                // case 4
                if (aabb1.max.y < aabb2.min.y) {
                    tFirst = max((aabb1.max.y - aabb2.min.y) / vRel.y, tFirst)
                }
                if (aabb1.min.y > aabb2.max.y) {
                    tLast = min((aabb1.min.y - aabb2.max.y) / vRel.y, tFirst)
                }
            }
            if (vRel.y > 0) {
                // case 2
                if (aabb1.min.y > aabb2.max.y) {
                    tFirst = max((aabb1.min.y - aabb2.max.y) / vRel.y, tFirst)
                }
                if (aabb1.max.y > aabb2.min.y) {
                    tLast = min((aabb1.max.y - aabb2.min.y) / vRel.y, tLast)
                }
                // case 3
                if (aabb1.max.y < aabb2.min.y) {
                    return false
                }
            }
            return tFirst <= tLast // Otherwise the rectangles intersect
        }
    }

}