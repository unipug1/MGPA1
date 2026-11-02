# scaffolding_app — the starting point

**DX1234 Mobile Game Programming · AY2026 S2**

The skeleton handed out in Week 2. It builds, runs, and does almost nothing —
which is the point. It gives you a project that already works so your first
week is spent writing your app, not fighting Gradle.

## What is here

| | |
|---|---|
| A `NavigationBar` with four tabs | `MainActivity.kt` |
| Tab icons as PNG images | `res/drawable-*dpi/` — see [`ICONS.md`](ICONS.md) |
| Pages 1–3 | `ui/pages/Pages123.kt` — placeholders |
| Page 4 | `ui/pages/Page4.kt` — a START button that is **not connected** |
| Material 3 theme | `ui/theme/` |

## What is deliberately missing

Everything you are marked on:

- No network call, no coroutines, no loading or error states *(Weeks 5–6)*
- No list, no reusable row component *(Week 4)*
- No input, no validation *(Week 6)*
- No animation *(Week 4)*
- **The START button on Page 4 does nothing** *(Week 9)*

## The START button

It is a `TODO`, on purpose.

Wiring it to a LibGDX Activity is Assignment 2, and it is taught in the
**Week 9** session — launching an Activity for a result, getting a value
back, what to do when there isn't one, and the lifecycle trap that leaks on
the second launch.

Work from those slides rather than guessing. The reference build is
`generic_app_minigame`.

## Build

```bash
./gradlew installDebug
```

## Two things already done right

Worth understanding before you change them:

**`rememberSaveable`, not `remember`** — the selected tab survives a rotation.
Assignment 1 is marked with a rotation.

**`Scaffold`'s padding is applied** — ignore it and your content hides behind
the navigation bar. This is the most common Week 4 bug.

## First thing to do

```bash
git init
git add .
git commit -m "scaffold builds and runs"
```

Your commit history is marked. Start it today.
