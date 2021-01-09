package be.goofydev.bridger.common.redis;

import io.lettuce.core.RedisNoScriptException;
import io.lettuce.core.ScriptOutputType;

public class LuaScript {
    private final String script;
    private final String hashed;
    private final RedisManager redisManager;

    public LuaScript(String script, String hashed, RedisManager redisManager) {
        this.script = script;
        this.hashed = hashed;
        this.redisManager = redisManager;
    }

    public <T> T evalCast() {
        return (T) this.eval();
    }

    public <T> T evalCast(String[] keys, String[] args) {
        return (T) this.eval(keys, args);
    }

    public Object eval() {
        return this.eval(new String[0], new String[0]);
    }

    public Object eval(String[] keys, String[] args) {
        return this.redisManager.execute(commands -> {
            try {
                return commands.evalsha(hashed, ScriptOutputType.MULTI, keys, args);
            } catch (RedisNoScriptException ex) {
                return commands.eval(this.script, ScriptOutputType.MULTI, keys, args);
            }
        });
    }
}