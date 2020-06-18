local call = redis.call

local liveKeys = call("KEYS", "proxy:heartbeats:*")
local total = 0

for _, value in pairs(liveKeys) do
    local onlineKey = string.gsub(value, "heartbeats", "online")
    total = total + call("SCARD", onlineKey)
end

return total