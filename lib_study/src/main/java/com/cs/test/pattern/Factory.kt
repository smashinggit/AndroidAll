package com.cs.test.pattern

/**
 *
 * @author  ChenSen
 * @date  2021/1/26
 * @desc 工厂模式
 **/


interface Car {
    fun move()
}

class Benz : Car {
    override fun move() {
        print("Benz move")
    }
}

class BMW : Car {
    override fun move() {
        print("BMW move")
    }
}

// 简单工厂
object SimpleFactory {

    fun getCar(type: Int): Car? {
        return when (type) {
            0 -> {
                Benz()
            }
            1 -> {
                BMW()
            }
            else -> null
        }
    }
}


//工厂方法模式

interface Factory {
    fun getCar(): Car
}


class BenzFactory : Factory {
    override fun getCar(): Car {
        return Benz()
    }
}

class BMWFactory : Factory {
    override fun getCar(): Car {
        return BMW()
    }
}

// 使用
val benzfactory = BenzFactory()
val car1 = benzfactory.getCar()
//car1.move()

val bmwFactory = BMWFactory()
val car2 = bmwFactory.getCar()
//car2.move()


//抽象工厂
interface Clothes {
    fun wear()
}

class Gucci : Clothes {
    override fun wear() {
        println("wear Gucci")
    }
}

class Prada : Clothes {
    override fun wear() {
        println("wear Prada ")
    }
}


interface AbstractFactory {
    fun getCar(): Car
    fun getClothes(): Clothes
}


class Zhangsan : AbstractFactory {
    override fun getCar(): Car {
        return Benz()
    }

    override fun getClothes(): Clothes {
        return Gucci()
    }
}

class LiSi : AbstractFactory {
    override fun getCar(): Car {
        return BMW()
    }

    override fun getClothes(): Clothes {
      return Prada()
    }
}




