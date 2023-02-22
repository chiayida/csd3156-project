package edu.singaporetech.services


open class Vector2(var x: Float = 0F, var y: Float = 0F) {
    fun copy(x: Float = this.x, y: Float = this.y) = Vector2(x, y)

    fun toZero() {
        x = 0F
        y = 0F
    }

    fun vec2String() : String {
        return "($x, $y)"
    }
}

// Parent class that is to be inherited by other classes
open class Entity {
    var position: Vector2 = Vector2(0F, 0F)
    var colliderScale : Vector2 = Vector2(1F, 1F)
    var speed: Float = 1F
    var velocity: Vector2 = Vector2(0F, 0F)

    fun getColliderMax() : Vector2 {
        val max = Vector2(position.x, position.y)
        max.x += colliderScale.x
        max.y += colliderScale.y
        return max
    }

    fun getColliderMin() : Vector2 {
        val min = Vector2(position.x, position.y)
        min.x -= colliderScale.x
        min.y -= colliderScale.y
        return min
    }

    open fun updatePosition(dt : Float) {
        position.x += velocity.x * dt
        position.y += velocity.y * dt
    }
}