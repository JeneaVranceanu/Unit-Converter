package converter

import java.lang.RuntimeException
import java.util.Scanner

class WrongUnit(message: String) : RuntimeException(message) {
    constructor(unit1: Unit, unit2: Unit) : this("Conversion from ${unit1.pluralName()} to ${unit2.pluralName()} is impossible")
}

sealed class Unit(vararg val unitSymbols: String) {
    abstract fun pluralName(): String
    abstract fun singleName(): String
    abstract fun simpleName(): String
    abstract fun toUnit(value: Double, unit: Unit): Double
    open fun getName(value: Double) = if (value == 1.0) singleName() else pluralName()

    abstract class Length(vararg unitSymbols: String) : Unit(*unitSymbols) {

        override fun toUnit(value: Double, unit: Unit): Double {
            if (value < 0.0) throw WrongUnit("Length shouldn't be negative")
            return value
        }

    }

    abstract class Mass(vararg unitSymbols: String) : Unit(*unitSymbols) {

        override fun toUnit(value: Double, unit: Unit): Double {
            if (value < 0.0) throw WrongUnit("Weight shouldn't be negative")
            return value
        }
    }

    object Kelvin : Unit("kelvin", "kelvins", "k") {
        override fun simpleName() = "Kelvins"
        override fun pluralName() = "Kelvins"
        override fun singleName() = "Kelvin"

        override fun toUnit(value: Double, unit: Unit) =
                when (unit) {
                    Kelvin -> value
                    Celsius -> value - 273.15
                    Fahrenheit -> value * 9.0 / 5.0 - 459.67
                    else -> throw WrongUnit(this, unit)
                }
    }

    object Fahrenheit : Unit("degree fahrenheit", "degrees fahrenheit", "fahrenheit", "df", "f") {
        override fun simpleName() = "Fahrenheit"
        override fun pluralName() = "degrees Fahrenheit"
        override fun singleName() = "degree Fahrenheit"

        override fun toUnit(value: Double, unit: Unit) =
                when (unit) {
                    Fahrenheit -> value
                    Celsius -> (value - 32.0) * 5.0 / 9.0
                    Kelvin -> (value + 459.67) * 5.0 / 9.0
                    else -> throw WrongUnit(this, unit)
                }
    }

    object Celsius : Unit("degree celsius", "degrees celsius", "celsius", "dc", "c") {
        override fun simpleName() = "Celsius"
        override fun pluralName() = "degrees Celsius"
        override fun singleName() = "degree Celsius"

        override fun toUnit(value: Double, unit: Unit) =
                when (unit) {
                    Celsius -> value
                    Kelvin -> value + 273.15
                    Fahrenheit -> value * 9.0 / 5.0 + 32.0
                    else -> throw WrongUnit(this, unit)
                }
    }

    object Millimeters : Length("mm", "millimeter", "millimeters") {
        override fun simpleName() = "millimeters"
        override fun pluralName() = "millimeters"
        override fun singleName() = "millimeter"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Kilometers, Centimeters, Meter -> Meter.toUnit(value / 1_000.0, unit)
                Millimeters -> value
                Mile, Yard, Foot, Inch -> Meter.toUnit(value / 1_000, unit)
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Centimeters : Length("cm", "centimeter", "centimeters") {
        override fun simpleName() = "centimeters"
        override fun pluralName() = "centimeters"
        override fun singleName() = "centimeter"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Kilometers, Millimeters, Meter -> Meter.toUnit(value / 100.0, unit)
                Centimeters -> value
                Mile, Yard, Foot, Inch -> Meter.toUnit(value / 100, unit)
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Meter : Length("meter", "meters", "m") {
        override fun simpleName() = "meters"
        override fun pluralName() = "meters"
        override fun singleName() = "meter"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Millimeters -> value * 1_000.0
                Centimeters -> value * 100.0
                Meter -> value
                Kilometers -> value / 1_000.0
                Mile -> value * 1609.35
                Yard -> value / 0.9144
                Foot -> value / 0.3048
                Inch -> value / 0.0254
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Kilometers : Length("km", "kilometer", "kilometers") {
        override fun simpleName() = "kilometers"
        override fun pluralName() = "kilometers"
        override fun singleName() = "kilometer"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Millimeters, Centimeters, Meter -> Meter.toUnit(value * 1_000.0, unit)
                Kilometers -> value
                Mile, Yard, Foot, Inch -> Meter.toUnit(value * 1_000, unit)
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Inch : Length("in", "″", "inch", "inches") {
        override fun simpleName() = "inches"
        override fun pluralName() = "inches"
        override fun singleName() = "inch"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Kilometers, Millimeters, Centimeters, Meter -> Meter.toUnit(value * 39.3701, unit)
                Mile -> value * 1.5_783e-5
                Yard -> value * 0.02_777_808
                Inch -> value
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Foot : Length("′", "foot", "feet", "ft") {
        override fun simpleName() = "feet"
        override fun pluralName() = "feet"
        override fun singleName() = "foot"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Millimeters, Centimeters, Meter, Kilometers ->
                    Inch.toUnit(value * 12.0, unit)
                Mile -> value / 5_280.0
                Yard -> value / 3.0
                Inch -> value * 12.0
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Yard : Length("yd", "yard", "yards") {
        override fun simpleName() = "yards"
        override fun pluralName() = "yards"
        override fun singleName() = "yard"


        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Millimeters, Centimeters, Meter, Kilometers ->
                    Inch.toUnit(value * 36.0, unit)
                Mile -> value / 1_760.0
                Yard -> value
                Inch -> value * 36.0
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Mile : Length("mi", "mile", "miles") {
        override fun simpleName() = "miles"
        override fun pluralName() = "miles"
        override fun singleName() = "mile"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Millimeters, Centimeters, Meter, Kilometers ->
                    Inch.toUnit(value * 63_360.0, unit)
                Mile -> value
                Yard -> value * 1_760.0
                Inch -> value * 63_360.0
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Gram : Mass("g", "gram", "grams") {
        override fun simpleName() = "grams"
        override fun pluralName() = "grams"
        override fun singleName() = "gram"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Kilogram -> value / 1_000.0
                Gram -> value
                Milligram -> value * 1_000.0
                Pound -> value / 453.592
                Ounce -> value / 28.3495
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Kilogram : Mass("kg", "kilogram", "kilograms") {
        override fun simpleName() = "kilograms"
        override fun pluralName() = "kilograms"
        override fun singleName() = "kilogram"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Kilogram -> value
                Gram -> value * 1_000.0
                Milligram -> value * 1_000_000.0
                Pound -> value * 2.20462
                Ounce -> value * 35.27399072294044
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Milligram : Mass("mg", "milligram", "milligrams") {
        override fun simpleName() = "milligrams"
        override fun pluralName() = "milligrams"
        override fun singleName() = "milligram"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Kilogram -> value / 1_000_000.0
                Gram -> value / 1_000.0
                Milligram -> value
                Pound -> value * 2.2046e-6
                Ounce -> value * 3.5274e-5
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Pound : Mass("lb", "pound", "pounds") {
        override fun simpleName() = "pounds"
        override fun pluralName() = "pounds"
        override fun singleName() = "pound"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Kilogram, Gram, Milligram -> Gram.toUnit(value * 453.592, unit)
                Ounce -> value * 16.0
                Pound -> value
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    object Ounce : Mass("oz", "ounce", "ounces") {
        override fun simpleName() = "ounces"
        override fun pluralName() = "ounces"
        override fun singleName() = "ounce"

        override fun toUnit(value: Double, unit: Unit): Double {
            super.toUnit(value, unit)

            return when (unit) {
                Kilogram, Gram, Milligram -> Gram.toUnit(value * 28.3495, unit)
                Pound -> value * 0.0625
                Ounce -> value
                else -> throw WrongUnit(this, unit)
            }
        }
    }

    class Unknown(val value: String) : Unit(value) {
        override fun simpleName() = "???"
        override fun pluralName() = "???"
        override fun singleName() = "???"
        override fun toUnit(value: Double, unit: Unit) = throw WrongUnit(this, unit)
    }

    companion object {
        fun forValue(unitName: String) =
                when (unitName.toLowerCase()) {
                    in Millimeters.unitSymbols -> Millimeters
                    in Centimeters.unitSymbols -> Centimeters
                    in Meter.unitSymbols -> Meter
                    in Kilometers.unitSymbols -> Kilometers
                    in Mile.unitSymbols -> Mile
                    in Yard.unitSymbols -> Yard
                    in Inch.unitSymbols -> Inch
                    in Foot.unitSymbols -> Foot
                    in Gram.unitSymbols -> Gram
                    in Kilogram.unitSymbols -> Kilogram
                    in Milligram.unitSymbols -> Milligram
                    in Pound.unitSymbols -> Pound
                    in Ounce.unitSymbols -> Ounce
                    in Celsius.unitSymbols -> Celsius
                    in Fahrenheit.unitSymbols -> Fahrenheit
                    in Kelvin.unitSymbols -> Kelvin
                    else -> Unknown(unitName)
                }
    }
}

fun main() {
    val scanner = Scanner(System.`in`)

    var isTerminated = false

    while (!isTerminated) {
        print("Enter what you want to convert (or exit): ")
        val line = scanner.nextLine().toLowerCase()
        isTerminated = line == "exit"

        if (!isTerminated) {
            var inputParts = dropPrepositionsAnd(line, "degrees", "degree")

            val unitValue = getUnitValue(inputParts)
            inputParts = inputParts.drop(1)

            val unit = Unit.forValue(inputParts.first())
            val toUnit = Unit.forValue(inputParts.last())

            when {
                unitValue != null -> {
                    try {
                        val result = unit.toUnit(unitValue, toUnit)
                        println("$unitValue ${unit.getName(unitValue)} is $result ${toUnit.getName(result)}")
                    } catch (error: WrongUnit) {
                        println(error.message)
                    }
                }
                else -> println("Parse error")
            }
        }
    }
}

fun getUnitValue(inputParts: List<String>) = inputParts.first().toDoubleOrNull()

fun dropPrepositionsAnd(fromLine: String, vararg toDrop: String): List<String> {
    @Suppress("NAME_SHADOWING")
    val toDrop = toDrop.toMutableList().let {
        it.add(" to ")
        it.add(" in ")
        it.add(" with ")
        it.add(" for ")
        it.add(" at ")
        it
    }

    var newLine = fromLine
    for (entry in toDrop) {
        newLine = newLine.replace(entry, " ")
    }

    return newLine.trim().split(" ").filter { !it.isBlank() }
}

