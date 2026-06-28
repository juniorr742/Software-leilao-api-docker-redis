local current_value = redis.call('HGET', KEYS[1], 'value')
local new_value = tonumber(ARGV[1])

if not current_value or new_value > tonumber(current_value) then
    redis.call('HSET', KEYS[1], 'value', ARGV[1], 'userId', ARGV[2])
        return 1
    else
        return 0
    end