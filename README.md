# Aurora (Fabric 1.21.11)

Client com menu (Right Shift), busca, abas (PvP, Visuals, HUD, Player, Optimizers, Misc),
Edit HUD, mira customizada, ceu de aurora e otimizadores de FPS.

## Compilar
- GitHub: suba os arquivos e rode o workflow "build" na aba Actions.
- Ou JDK 21 + Gradle 9.2.1: `gradle build` -> `build/libs/aurora-0.1.0.jar`

## Fundo do menu
Misc > Customize Menu: Background = Purple (padrao) ou Aurora (imagem em assets/aurorahud/aurora_bg.png).

## Fonte
O menu usa a fonte Outfit (SIL OFL 1.1, ver assets/aurorahud/font/OFL.txt).
Para trocar o tamanho, edite "size" em assets/aurorahud/font/ui.json.

## Adicionar um modulo
1. Crie uma classe em `modules/` que estende `Module`.
2. Registre em `ModuleManager.init()`.
