# rs-utils
A collection of my favorite reusable utils

## net.rockscience.util.date.MultizoneDate
This is a very handy wrapper around a ZonedDateTime.  Lots of convenient null-safe static methods for building and converting dates in to different zones

## net.rockscience.util.enumz.StableOrderEnum 
A base class for enums that can provice a stable numeric code even if you re-order the enums values. This is really handy for persistence.  It has null-safe fromCode and toCode static methods for converting to and from the stable ordinal number

## net.rockscience.util.image.ImageUtil
Utils for resizing and streaming images

## net.rockscience.util.string.DelimitedStringList
Handly helper class for converting between lists of string and string lists.

## net.rockscience.util.RetryTimer
Helper class for computing times for retries.  

## net.rockscience.util.TaskCron
Helper for modeling and computing the periodic time to do something

##net.rockscience.util.Timespan
Logging Helper to log the time somee operation took
