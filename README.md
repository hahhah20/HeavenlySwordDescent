# Heavenly Sword Descent（天剑降临）

Minecraft Paper 技能插件工程。

## 技能说明
天空生成一把巨剑，短暂蓄力后高速坠落，对落点造成分层范围伤害与击退；巨剑落地后继续驻留并周期性造成持续伤害。

## V2.2.0
- 技能流程拆分为 `HeavenlySwordSkill` 编排层。
- 新增 `HeavenlySwordEntity` 实体生命周期模块。
- 新增 `SwordModelController` ItemDisplay 模型模块。
- 新增 `SwordEffectManager` 统一特效入口。
- 新增 `SwordDamageHandler` 统一伤害入口。
- 新增 `ModuleConfig` / `SkillConfig` 配置访问层。
- 新增 `skills.yml`、`sword.yml`、`effects.yml` 三份模块配置。
- 移除旧 `SwordProjectile` 实现。
- 保留 V2.1.20 的 `+135° Z` 固定模型姿态与仅水平面向释放者逻辑。
- 保留蓄力、坠落、冲击、分层伤害、驻留与持续伤害能力。

## 构建目标
- Paper 1.21.x
- Java 21
- Maven

详细结构与测试清单见 `docs/V2.2.0.md`。
