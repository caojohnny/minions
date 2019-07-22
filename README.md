# `minions`

This isn't the first time I've worked on a minions plugin,
or the second, or even third time. Hopefully, it I can get
it 
[done right this time](https://agenttroll.github.io/blog/2018/03/25/those-mistakes-are-mine.html)
though.

I'm again bored, and though I have two more "projects" 
(more like chores really) in the burner, I figure this
project shouldn't take too much time, and it would be nice
again to brush up on *anything* above the 1.8.8 Bukkit API.
A buddy of mine is working on something very similar, but
this is an entirely independent project.

For the uninitiated few (or lot, I don't really know), the
idea of minions is to have a "helper" or a "worker" of 
sorts to do repetitive tasks. For example, when I worked on
the original MineSaga minions plugin, some of those tasks
were mining blocks or farming mobs.

# Building

``` shell
git clone https://github.com/AgentTroll/minions.git
cd minions
mvn clean install
```

Requires Spigot (**not** the API) 1.14.2 installed to your
local maven repository.

# Features

- Armor stand minion entities
- Sneak+right click to give/take pickaxes
- Breaks blocks using the correct mining speed
- Collects the correct block experience
- Block break animation (on the block, not the arm swing)
- Persists over restarts
- Nameable

# Notes

- Does not come with lang support
- Only supports 1.14 up, not that hard to port down though

# Credits

Built with [IntelliJ IDEA](https://www.jetbrains.com/idea/)
