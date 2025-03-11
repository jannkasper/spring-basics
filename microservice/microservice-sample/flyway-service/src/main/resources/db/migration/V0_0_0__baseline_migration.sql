-- 1. Create the users table
CREATE TABLE IF NOT EXISTS public.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 2. Create roles table
CREATE TABLE IF NOT EXISTS public.roles (
    name VARCHAR(50) PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 4. Create user_roles (Many-to-Many relationship)
CREATE TABLE IF NOT EXISTS public.user_roles (
    user_id INT NOT NULL,
    role_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(name) ON DELETE CASCADE
    );

-- 5. Insert default roles
INSERT INTO public.roles (name, role_name) 
VALUES ('ROLE_USER', 'Regular User') 
ON CONFLICT (name) DO NOTHING;

INSERT INTO public.roles (name, role_name) 
VALUES ('ROLE_ADMIN', 'Administrator') 
ON CONFLICT (name) DO NOTHING;

-- 6. Insert users with BCrypt encoded passwords
-- Password: admin123
INSERT INTO public.users (username, password, email)
VALUES ('admin', '$2a$10$7QZi.nI3zUXebA/1zFOyxOd.YE9zV/GcLq5db8t/YWlNeAUnmdSxS', 'admin@example.com')
    ON CONFLICT (username) DO NOTHING;
    
-- Password: user123
INSERT INTO public.users (username, password, email)
VALUES ('user', '$2a$10$EqPU1Kv2kBt85KBcAVWXFe2x9aW0YO1gV3Uci4dZKeXyQ5YfzG3MK', 'user@example.com')
    ON CONFLICT (username) DO NOTHING;
    
-- Password: testadmin
INSERT INTO public.users (username, password, email)
VALUES ('testadmin', '$2a$10$M2S2f8mYILvO1I3BnsKTAugW6b7byAZGLIma6jgcGLM4mVlUg6JyW', 'testadmin@example.com')
    ON CONFLICT (username) DO NOTHING;
    
-- Password: testuser
INSERT INTO public.users (username, password, email)
VALUES ('testuser', '$2a$10$5Yl6Bz7HVC42uKCcM1EYfecP8zqPFTwEvX7yjxJKyXl13i1WoTmYi', 'testuser@example.com')
    ON CONFLICT (username) DO NOTHING;

-- 7. Assign roles to users
-- Assign admin role to admin user
INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, r.name FROM public.users u, roles r WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
    ON CONFLICT (user_id, role_id) DO NOTHING;
    
-- Assign user role to user
INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, r.name FROM public.users u, roles r WHERE u.username = 'user' AND r.name = 'ROLE_USER'
    ON CONFLICT (user_id, role_id) DO NOTHING;
    
-- Assign admin role to testadmin
INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, r.name FROM public.users u, roles r WHERE u.username = 'testadmin' AND r.name = 'ROLE_ADMIN'
    ON CONFLICT (user_id, role_id) DO NOTHING;
    
-- Assign user role to testuser
INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, r.name FROM public.users u, roles r WHERE u.username = 'testuser' AND r.name = 'ROLE_USER'
    ON CONFLICT (user_id, role_id) DO NOTHING;
